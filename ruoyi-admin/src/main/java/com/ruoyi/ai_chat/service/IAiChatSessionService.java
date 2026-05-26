package com.ruoyi.ai_chat.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ai_chat.domain.AiChatMessage;
import com.ruoyi.ai_chat.domain.AiChatSession;

/**
 * AI聊天会话Service接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
public interface IAiChatSessionService extends IService<AiChatSession>
{
    /**
     * 查询AI聊天会话
     * 
     * @param sessionId AI聊天会话主键
     * @return AI聊天会话
     */
    public AiChatSession selectAiChatSessionBySessionId(Long sessionId);

    /**
     * 查询AI聊天会话列表
     * 
     * @param aiChatSession AI聊天会话
     * @return AI聊天会话集合
     */
    public List<AiChatSession> selectAiChatSessionList(AiChatSession aiChatSession);

    /**
     * 新增AI聊天会话
     * 
     * @param aiChatSession AI聊天会话
     * @return 结果
     */
    public int insertAiChatSession(AiChatSession aiChatSession);

    /**
     * 修改AI聊天会话
     * 
     * @param aiChatSession AI聊天会话
     * @return 结果
     */
    public int updateAiChatSession(AiChatSession aiChatSession);

    /**
     * 批量删除AI聊天会话
     * 
     * @param sessionIds 需要删除的AI聊天会话主键集合
     * @return 结果
     */
    public int deleteAiChatSessionBySessionIds(Long[] sessionIds);

    /**
     * 删除AI聊天会话信息
     * 
     * @param sessionId AI聊天会话主键
     * @return 结果
     */
    public int deleteAiChatSessionBySessionId(Long sessionId);

    /**
     * AI聊天会话Service接口
     *
     * @author ruoyi
     * @date 2026-04-20
     */
     AiChatSession saveSession(Long userId , String question);
    /**
     * 获取会话
     */
    List<AiChatSession> getSession(Long userId);

    /**
     *删除当前会话
     */
    boolean softDeleteSession(Long sessionId, Long userId);


    /**
     * AI聊天消息Service接口
     *
     * @author ruoyi
     * @date 2026-04-20
     */

    interface IAiChatMessageService
    {
        /**
         * 查询AI聊天消息
         *
         * @param messageId AI聊天消息主键
         * @return AI聊天消息
         */
        public AiChatMessage selectAiChatMessageByMessageId(Long messageId);

        /**
         * 查询AI聊天消息列表
         *
         * @param aiChatMessage AI聊天消息
         * @return AI聊天消息集合
         */
        public List<AiChatMessage> selectAiChatMessageList(AiChatMessage aiChatMessage);

        /**
         * 新增AI聊天消息
         *
         * @param aiChatMessage AI聊天消息
         * @return 结果
         */
        public int insertAiChatMessage(AiChatMessage aiChatMessage);

        /**
         * 修改AI聊天消息
         *
         * @param aiChatMessage AI聊天消息
         * @return 结果
         */
        public int updateAiChatMessage(AiChatMessage aiChatMessage);

        /**
         * 批量删除AI聊天消息
         *
         * @param messageIds 需要删除的AI聊天消息主键集合
         * @return 结果
         */
        public int deleteAiChatMessageByMessageIds(Long[] messageIds);

        /**
         * 删除AI聊天消息信息
         *
         * @param messageId AI聊天消息主键
         * @return 结果
         */
        public int deleteAiChatMessageByMessageId(Long messageId);
        /**
         * 存储聊天信息
         * @param sessionid
         */

        AiChatMessage saveAiChat(Long sessionid ,long userid, String role,String content,int tokens);

        /**
         * 获取当前会话的聊天记录
         */
        List<AiChatMessage> getSessionChat(Long sessionId , Long userId);
        /**
         *缓存当前上下文记录
         */
        void loadChatHistoryToRedis(Long userId, Long sessionId);
        /**
         * 保存聊天消息到数据库和Redis
         *
         * @param userId    用户ID
         * @param sessionId 会话ID
         * @param question  用户问题
         * @param answer    AI回答
         * @param status    消息状态（1-成功，0-失败）
         */
        void saveChatMessage(Long userId, Long sessionId, String question,
                                     String answer, Integer status);

    }
}
