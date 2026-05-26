package com.ruoyi.ai_chat.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * AI聊天消息对象 ai_chat_message
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Data
public class AiChatMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID，主键自增 */
    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    /** 所属会话ID，关联ai_chat_session.session_id */
    @Excel(name = "所属会话ID，关联ai_chat_session.session_id")
    private Long sessionId;

    /** 用户ID，关联sys_user.user_id（冗余字段，方便查询） */
    @Excel(name = "用户ID，关联sys_user.user_id", readConverterExp = "冗=余字段，方便查询")
    private Long userId;

    /** 角色：user用户消息 assistantAI回复消息 */
    @Excel(name = "角色：user用户消息 assistantAI回复消息")
    private String role;

    /** 消息内容，用户问题或AI回答 */
    @Excel(name = "消息内容，用户问题或AI回答")
    private String content;

    /** 消耗的token数，用于统计和计费 */
    @Excel(name = "消耗的token数，用于统计和计费")
    private int tokens;

    /** 创建时间 */
    @Excel(name = "创建时间")
    private Date createTime;

    @TableField(exist = false)
    private Date updateTime;
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("messageId", getMessageId())
            .append("sessionId", getSessionId())
            .append("userId", getUserId())
            .append("role", getRole())
            .append("content", getContent())
            .append("tokens", getTokens())
            .append("createTime", getCreateTime())
            .toString();
    }
}
