package com.ruoyi.blog_comment.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 博客评论对象 blog_comment
 * 
 * @author ruoyi
 * @date 2026-04-14
 */
@Data
public class BlogComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID（雪花算法） */
    private Long id;

    /** 文章ID（关联blog_article.id） */
    @Excel(name = "文章ID", readConverterExp = "关=联blog_article.id")
    private Long articleId;

    /** 用户ID（关联sys_user.user_id） */
    @Excel(name = "用户ID", readConverterExp = "关=联sys_user.user_id")
    private Long userId;

    /** 父评论ID（0表示顶级评论） */
    @Excel(name = "父评论ID", readConverterExp = "0=表示顶级评论")
    private Long parentId;

    /** 回复的目标用户ID */
    @Excel(name = "回复的目标用户ID")
    private Long replyToUserId;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 状态（0正常 1隐藏 2删除） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=隐藏,2=删除")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("articleId", getArticleId())
            .append("userId", getUserId())
            .append("parentId", getParentId())
            .append("replyToUserId", getReplyToUserId())
            .append("content", getContent())
            .append("likeCount", getLikeCount())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
