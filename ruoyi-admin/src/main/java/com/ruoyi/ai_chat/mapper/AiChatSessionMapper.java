package com.ruoyi.ai_chat.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ai_chat.domain.AiChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * AI聊天会话Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSession>
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
     * 删除AI聊天会话
     * 
     * @param sessionId AI聊天会话主键
     * @return 结果
     */
    public int deleteAiChatSessionBySessionId(Long sessionId);

    /**
     * 批量删除AI聊天会话
     * 
     * @param sessionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiChatSessionBySessionIds(Long[] sessionIds);

// 通过用户id查询性格id，如果没有则返回默认id
    @Select("SELECT COALESCE(" +
            "   (SELECT personality_id FROM ai_personality WHERE user_id = #{userId} AND status = '0' AND is_active = '1'), " +
            "   (SELECT personality_id FROM ai_personality WHERE user_id = 0 AND status = '0' LIMIT 1) " +
            ")")
    Long selectPersonalityIdByUserId(Long userId);


}
