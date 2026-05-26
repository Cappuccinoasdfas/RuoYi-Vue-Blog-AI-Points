package com.ruoyi.ai_chat.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI聊天会话对象 ai_chat_session
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Data
public class AiChatSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会话ID，主键自增 */
    @TableId(value = "session_id", type = IdType.AUTO)
    private Long sessionId;

    /** 用户ID，关联sys_user.user_id */
    @Excel(name = "用户ID，关联sys_user.user_id")
    private Long userId;

    /** 使用的性格ID，关联ai_personality.personality_id */
    @Excel(name = "使用的性格ID，关联ai_personality.personality_id")
    private Long personalityId;

    /** 会话名称，自动生成或用户修改 */
    @Excel(name = "会话名称，自动生成或用户修改")
    private String sessionName;

    /** 状态：0正常 1删除（软删除） */
    @Excel(name = "状态：0正常 1删除", readConverterExp = "软=删除")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("sessionId", getSessionId())
            .append("userId", getUserId())
            .append("personalityId", getPersonalityId())
            .append("sessionName", getSessionName())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
