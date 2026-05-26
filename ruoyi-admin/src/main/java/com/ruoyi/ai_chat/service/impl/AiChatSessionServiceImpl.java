package com.ruoyi.ai_chat.service.impl;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ai_chat.mapper.AiChatMessageMapper;
import com.ruoyi.common.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_chat.mapper.AiChatSessionMapper;
import com.ruoyi.ai_chat.domain.AiChatSession;
import com.ruoyi.ai_chat.service.IAiChatSessionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * AI聊天会话Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Service
@RequiredArgsConstructor
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession> implements IAiChatSessionService
{
    private final AiChatSessionMapper aiChatSessionMapper;

    /**
     * 查询AI聊天会话
     * 
     * @param sessionId AI聊天会话主键
     * @return AI聊天会话
     */
    @Override
    public AiChatSession selectAiChatSessionBySessionId(Long sessionId)
    {
        return aiChatSessionMapper.selectAiChatSessionBySessionId(sessionId);
    }

    /**
     * 查询AI聊天会话列表
     * 
     * @param aiChatSession AI聊天会话
     * @return AI聊天会话
     */
    @Override
    public List<AiChatSession> selectAiChatSessionList(AiChatSession aiChatSession)
    {
        return aiChatSessionMapper.selectAiChatSessionList(aiChatSession);
    }

    /**
     * 新增AI聊天会话
     * 
     * @param aiChatSession AI聊天会话
     * @return 结果
     */
    @Override
    public int insertAiChatSession(AiChatSession aiChatSession)
    {
        aiChatSession.setCreateTime(DateUtils.getNowDate());
        return aiChatSessionMapper.insertAiChatSession(aiChatSession);
    }

    /**
     * 修改AI聊天会话
     * 
     * @param aiChatSession AI聊天会话
     * @return 结果
     */
    @Override
    public int updateAiChatSession(AiChatSession aiChatSession)
    {
        aiChatSession.setUpdateTime(DateUtils.getNowDate());
        return aiChatSessionMapper.updateAiChatSession(aiChatSession);
    }

    /**
     * 批量删除AI聊天会话
     * 
     * @param sessionIds 需要删除的AI聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteAiChatSessionBySessionIds(Long[] sessionIds)
    {
        return aiChatSessionMapper.deleteAiChatSessionBySessionIds(sessionIds);
    }

    /**
     * 删除AI聊天会话信息
     * 
     * @param sessionId AI聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteAiChatSessionBySessionId(Long sessionId)
    {
        return aiChatSessionMapper.deleteAiChatSessionBySessionId(sessionId);
    }

    /**
     * 储存会话窗口
     * @param userId 用户id
     * @param question  问题
     * @return
     */
    @Override
    public AiChatSession saveSession(Long userId, String question) {
        // 获取性格ID（自动带默认值）
        Long personalityId = baseMapper.selectPersonalityIdByUserId(userId);
        AiChatSession session = new AiChatSession();
        session.setUserId(userId);
        session.setSessionName(question.length() > 50 ? question.substring(0, 10) : question);
        session.setPersonalityId(personalityId);
        save(session);
        return session;
    }

    /**
     * 获取会话窗口
     * @param userId 用户id
     * @return
     */
    @Override
    public List<AiChatSession> getSession(Long userId) {
        QueryWrapper<AiChatSession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("status", "0");
        return list(queryWrapper);
    }


    // 软删除会话（推荐，不真正删除数据）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean softDeleteSession(Long sessionId, Long userId) {
        // 验证权限
        AiChatSession session = this.getById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            return false;
        }

        // 软删除会话
        LambdaUpdateWrapper<AiChatSession> sessionWrapper = new LambdaUpdateWrapper<>();
        sessionWrapper.eq(AiChatSession::getSessionId, sessionId)
                .set(AiChatSession::getStatus, "1")  // 1 表示已删除
                .set(AiChatSession::getUpdateTime, new Date());
        boolean sessionResult = this.update(sessionWrapper);

        return sessionResult;
    }
}
