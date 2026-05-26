package com.ruoyi.ai_chat.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ai_chat.domain.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI聊天消息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage>
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
     * 删除AI聊天消息
     * 
     * @param messageId AI聊天消息主键
     * @return 结果
     */
    public int deleteAiChatMessageByMessageId(Long messageId);

    /**
     * 批量删除AI聊天消息
     * 
     * @param messageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiChatMessageByMessageIds(Long[] messageIds);

}
