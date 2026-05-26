package com.ruoyi.ai_chat.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * AI性格对象 ai_personality
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Data
public class AiPersonality
        extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 性格ID，主键自增 */
    private Long personalityId;

    /** 所属用户ID，关联sys_user.user_id */
    @Excel(name = "所属用户ID，关联sys_user.user_id")
    private Long userId;

    /** 性格名称，用户自定义，如：幽默助手 */
    @Excel(name = "性格名称，用户自定义，如：幽默助手")
    private String name;

    /** 性格描述内容，限制200字以内 */
    @Excel(name = "性格描述内容，限制200字以内")
    private String content;

    /** 首次打招呼语，如：嗨！今天想聊点什么？ */
    @Excel(name = "首次打招呼语，如：嗨！今天想聊点什么？")
    private String greeting;

    /** 是否当前启用：0否 1是（每个用户只有一个启用） */
    @Excel(name = "是否当前启用：0否 1是", readConverterExp = "每=个用户只有一个启用")
    private String isActive;

    /** 状态：0正常 1删除（软删除） */
    @Excel(name = "状态：0正常 1删除", readConverterExp = "软=删除")
    private String status;

    @TableField(exist = false)
    private Date updateTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("personalityId", getPersonalityId())
            .append("userId", getUserId())
            .append("name", getName())
            .append("content", getContent())
            .append("greeting", getGreeting())
            .append("isActive", getIsActive())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
