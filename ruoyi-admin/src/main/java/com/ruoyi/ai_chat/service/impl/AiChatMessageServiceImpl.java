package com.ruoyi.ai_chat.service.impl;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ai_chat.domain.AiConfigProperties;
import com.ruoyi.ai_chat.service.IAiChatSessionService;
import com.ruoyi.common.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_chat.mapper.AiChatMessageMapper;
import com.ruoyi.ai_chat.domain.AiChatMessage;

/**
 * AI聊天消息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Service
@RequiredArgsConstructor
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements IAiChatSessionService.IAiChatMessageService
{
    private final AiChatMessageMapper aiChatMessageMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AiConfigProperties aiConfig;
    /**
     * 查询AI聊天消息
     * 
     * @param messageId AI聊天消息主键
     * @return AI聊天消息
     */
    @Override
    public AiChatMessage selectAiChatMessageByMessageId(Long messageId)
    {
        return aiChatMessageMapper.selectAiChatMessageByMessageId(messageId);
    }

    /**
     * 查询AI聊天消息列表
     * 
     * @param aiChatMessage AI聊天消息
     * @return AI聊天消息
     */
    @Override
    public List<AiChatMessage> selectAiChatMessageList(AiChatMessage aiChatMessage)
    {
        return aiChatMessageMapper.selectAiChatMessageList(aiChatMessage);
    }

    /**
     * 新增AI聊天消息
     * 
     * @param aiChatMessage AI聊天消息
     * @return 结果
     */
    @Override
    public int insertAiChatMessage(AiChatMessage aiChatMessage)
    {
        aiChatMessage.setCreateTime(DateUtils.getNowDate());
        return aiChatMessageMapper.insertAiChatMessage(aiChatMessage);
    }

    /**
     * 修改AI聊天消息
     * 
     * @param aiChatMessage AI聊天消息
     * @return 结果
     */
    @Override
    public int updateAiChatMessage(AiChatMessage aiChatMessage)
    {
        return aiChatMessageMapper.updateAiChatMessage(aiChatMessage);
    }

    /**
     * 批量删除AI聊天消息
     * 
     * @param messageIds 需要删除的AI聊天消息主键
     * @return 结果
     */
    @Override
    public int deleteAiChatMessageByMessageIds(Long[] messageIds)
    {
        return aiChatMessageMapper.deleteAiChatMessageByMessageIds(messageIds);
    }

    /**
     * 删除AI聊天消息信息
     * 
     * @param messageId AI聊天消息主键
     * @return 结果
     */
    @Override
    public int deleteAiChatMessageByMessageId(Long messageId)
    {
        return aiChatMessageMapper.deleteAiChatMessageByMessageId(messageId);
    }

    /**存储聊天信息
     * @param sessionid
     */
    @Override
    public AiChatMessage saveAiChat(Long sessionid ,long userid, String role,String content,int tokens){
        AiChatMessage aiChatMessage = new AiChatMessage();
        aiChatMessage.setSessionId(sessionid);
        aiChatMessage.setUserId(userid);
        aiChatMessage.setRole(role);
        aiChatMessage.setContent(content);
        aiChatMessage.setTokens(tokens);
        save(aiChatMessage);
        return aiChatMessage;
    }

    /**
     * 查询当前会话id关联的聊天记录
     * @
     */
    @Override
    public List<AiChatMessage> getSessionChat(Long sessionId , Long userId){
        QueryWrapper<AiChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id",sessionId);
        queryWrapper.eq("user_id",userId);
        return aiChatMessageMapper.selectList(queryWrapper);
    }

    @Override
    public void loadChatHistoryToRedis(Long userId, Long sessionId) {
        String key = String.format("ChatAI:user:%s:session:%s", userId, sessionId);

        // Redis已有缓存，直接返回
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return;
        }

        // 从数据库查询历史消息
        List<AiChatMessage> messages = getSessionChat(sessionId, userId);
        if (messages == null || messages.isEmpty()) {
            return;
        }

        // 按创建时间排序，确保对话顺序正确
        messages.sort(Comparator.comparing(AiChatMessage::getCreateTime));

        // 转换为JSON字符串列表
        List<String> jsonList = messages.stream()
                .map(JSON::toJSONString)
                .collect(Collectors.toList());

        // 存入Redis List（右侧推入，保持时间顺序）
        int ttlHours = aiConfig.getCache().getChatHistoryTtlHours();
        stringRedisTemplate.opsForList().rightPushAll(key, jsonList);
        stringRedisTemplate.expire(key, ttlHours, TimeUnit.HOURS);
    }




    /**
     * 保存聊天消息到数据库和Redis
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param question  用户问题
     * @param answer    AI回答
     * @param status    消息状态（1-成功，0-失败）
     */
    public void saveChatMessage(Long userId, Long sessionId, String question,
                                 String answer, Integer status) {
        // 保存到MySQL数据库
        AiChatMessage msg = saveAiChat(
                sessionId, userId, question, answer, status);

        // 追加到Redis List末尾，供Python下次读取历史记录
        String key = String.format("ChatAI:user:%s:session:%s", userId, sessionId);
        stringRedisTemplate.opsForList().rightPush(key, JSON.toJSONString(msg));

        // 刷新过期时间
        int ttlHours = aiConfig.getCache().getChatHistoryTtlHours();
        stringRedisTemplate.expire(key, ttlHours, TimeUnit.HOURS);
    }
}
