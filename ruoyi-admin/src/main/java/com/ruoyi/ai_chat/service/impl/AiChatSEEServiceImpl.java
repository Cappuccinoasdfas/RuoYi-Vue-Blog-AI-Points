package com.ruoyi.ai_chat.service.impl;
import com.ruoyi.ai_chat.domain.AiConfigProperties;
import com.ruoyi.ai_chat.service.IAiChatService;
import com.ruoyi.ai_chat.service.IAiChatSessionService;
import com.ruoyi.ai_chat.service.python.PythonChatClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class AiChatSEEServiceImpl implements IAiChatService {
    private final IAiChatSessionService.IAiChatMessageService aiChatMessageService;
    private final RedissonClient redissonClient;
    private final AiConfigProperties aiConfig;
    private final PythonChatClient pythonChatClient;
    private final IAiChatSessionService aiChatSessionService;
    /**
     * 流式对话接口 - 使用SSE进行实时响应
     * 使用分布式锁防止同一用户并发对话，保证数据一致性
     *
     * @param userId    用户ID
     * @param question  用户问题
     * @param sessionId 会话ID（可选，为空时自动创建新会话）
     * @return SseEmitter SSE连接对象
     */
    @Override
    public SseEmitter chatStream(Long userId, String question, Long sessionId) {
        SseEmitter emitter = new SseEmitter(aiConfig.getSse().getTimeout());

        String lockKey = "lock:user:chat:" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        // ========== 主线程加锁 ==========
        if (!tryAcquireLock(lock, emitter)) {
            return emitter;
        }

        try {
            Long finalSessionId = prepareSession(userId, question, sessionId);
            //加载上下文
            aiChatMessageService.loadChatHistoryToRedis(userId, finalSessionId);
            CompletableFuture.runAsync(() -> {
                try {
                    handshakeWithPython(userId, question, finalSessionId, emitter, lock);
                } catch (Exception e) {
                    handlePythonError(e, userId, question, finalSessionId, emitter, lock);
                }
            });

        } catch (Exception e) {
            releaseLockIfHeld(lock);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 尝试获取分布式锁
     * 配置了wait-time和lease-time，锁到期自动释放，看门狗机制被禁用
     *
     * @param lock    Redisson分布式锁对象
     * @param emitter SSE连接对象，用于获取锁失败时返回错误
     * @return true-获取锁成功，false-获取锁失败
     */
    private boolean tryAcquireLock(RLock lock, SseEmitter emitter) {
        try {
            // 从配置文件读取锁参数
            int waitTime = aiConfig.getLock().getWaitTime();
            int leaseTime = aiConfig.getLock().getLeaseTime();

            log.info("===========================================");
            log.info("线程: {} 尝试获取锁", Thread.currentThread().getName());

            // 尝试获取锁：最多等待waitTime秒，锁持有leaseTime秒后自动释放
            if (lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                log.info("线程: {} ============ 获取锁成功！============", Thread.currentThread().getName());
                return true;
            }

            // 获取锁失败：用户已有对话正在处理中
            log.info("线程: {} 获取锁失败！", Thread.currentThread().getName());
            log.info("===========================================");

            sendErrorAndComplete(emitter, "您有一个对话正在处理中，请稍后再试");
            return false;

        } catch (InterruptedException e) {
            // 线程被中断，恢复中断状态
            Thread.currentThread().interrupt();
            sendErrorAndComplete(emitter, "系统繁忙，请稍后再试");
            return false;
        }
    }

    /**
     * 准备会话信息
     * 如果传入了sessionId则直接使用，否则创建新会话
     *
     * @param userId    用户ID
     * @param question  用户问题（用于新会话的标题）
     * @param sessionId 会话ID（可为null）
     * @return 最终的会话ID
     */
    private Long prepareSession(Long userId, String question, Long sessionId) {
        return sessionId != null ? sessionId
                : aiChatSessionService.saveSession(userId, question).getSessionId();
    }


    /**
     * 安全释放分布式锁
     * 使用forceUnlock强制释放，避免因加锁/解锁线程不一致导致释放失败
     *
     * @param lock Redisson分布式锁对象
     */
    private void releaseLockIfHeld(RLock lock) {
        try {
            lock.forceUnlock();
        } catch (Exception e) {
            // 锁可能已过期被Redis自动删除，忽略异常
        }
    }

    /**
     * 发送错误消息并关闭SSE连接
     *
     * @param emitter      SSE连接对象
     * @param errorMessage 错误提示信息
     */
    private void sendErrorAndComplete(SseEmitter emitter, String errorMessage) {
        try {
            // 发送error事件到前端
            emitter.send(SseEmitter.event().name("error").data(errorMessage));
            // 正常关闭连接
            emitter.complete();
        } catch (IOException e) {
            // 发送失败时，直接以错误状态关闭
            emitter.completeWithError(e);
        }
    }
    /**
     * 与Python服务握手，处理流式对话
     * 通过SSE逐token返回AI生成的回答，并在完成时保存消息、释放锁
     *
     * @param userId    用户ID
     * @param question  用户问题
     * @param sessionId 会话ID
     * @param emitter   SSE连接对象
     * @param lock      分布式锁对象（对话完成后释放）
     */
    public void handshakeWithPython(Long userId, String question, Long sessionId,
                                     SseEmitter emitter, RLock lock) {
        // 收集完整回答
        StringBuilder fullAnswer = new StringBuilder();
        // 防止重复完成
        final boolean[] finished = {false};

        try {
            pythonChatClient.sendStreamRequest(userId, question, sessionId,
                    // onToken：收到流式token，推送给前端
                    token -> {
                        fullAnswer.append(token);
                        try {
                            if (!finished[0]) {
                                emitter.send(SseEmitter.event().data(token));
                            }
                        } catch (IOException e) {
                            log.error("发送token失败", e);
                        }
                    },
                    // onComplete：对话完成，保存消息、关闭SSE、释放锁
                    () -> {
                        if (finished[0]) return;  // 已处理过，跳过
                        finished[0] = true;

                        // 保存成功消息
                        aiChatMessageService.saveChatMessage(userId, sessionId, question, fullAnswer.toString(), 1);

                        try {
                            // 发送完成事件，关闭SSE连接
                            emitter.send(SseEmitter.event().name("done"));
                            emitter.complete();
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        } finally {
                            // 释放分布式锁，允许下次对话
                            releaseLockIfHeld(lock);
                        }
                    },
                    // onError：Python服务异常，保存错误消息、关闭SSE、释放锁
                    e -> {
                        if (finished[0]) return;  // 已处理过，跳过
                        finished[0] = true;

                        handlePythonError(e, userId, question, sessionId, emitter, lock);
                        releaseLockIfHeld(lock);
                    }
            );
        } catch (Exception e) {
            // sendStreamRequest本身抛出的异常
            if (!finished[0]) {
                finished[0] = true;
                handlePythonError(e, userId, question, sessionId, emitter, lock);
            }
        }
    }

    /**
     * 处理Python服务调用异常
     * 保存错误消息到数据库和Redis，向前端返回友好提示，并释放锁
     *
     * @param e         异常对象
     * @param userId    用户ID
     * @param question  用户问题
     * @param sessionId 会话ID
     * @param emitter   SSE连接对象
     * @param lock      分布式锁对象
     */
    private void handlePythonError(Exception e, Long userId, String question,
                                   Long sessionId, SseEmitter emitter, RLock lock) {
        log.error("Python服务调用失败", e);

        // 保存错误消息（status=0表示失败）
        aiChatMessageService.saveChatMessage(userId, sessionId, question, "网络出问题了呢！稍后再回来呀", 0);

        // 向前端返回错误提示
        sendErrorAndComplete(emitter, "网络出问题了呢！稍后再回来呀");

        // 释放分布式锁
        releaseLockIfHeld(lock);
    }

}
