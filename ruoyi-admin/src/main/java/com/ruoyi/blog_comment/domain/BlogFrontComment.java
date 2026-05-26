package com.ruoyi.blog_comment.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("blog_comment")
public class BlogFrontComment {
    /** 评论ID（雪花算法） */
    private String id;

    /** 文章ID（关联blog_article.id） */
    @Excel(name = "文章ID")
    private Long articleId;

    /** 用户ID（关联sys_user.user_id） */
    @Excel(name = "用户ID")
    private Long userId;

    /** 父评论ID（0表示顶级评论） */
    @Excel(name = "父评论ID")
    private String  parentId;

    /** 回复的目标用户ID */
    @Excel(name = "回复的目标用户ID")
    private Long replyToUserId;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;

    /** 状态（0正常 1隐藏 2删除） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=隐藏,2=删除")
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ⭐ 这些字段不在数据库表中，用于接收关联查询结果

    /** 评论名字 */
    @TableField(exist = false)
    private String userName;

    /** 评论头像 */
    @TableField(exist = false)
    private String userAvatar;

    /** 回复目标用户名 */
    @TableField(exist = false)
    private String replyToUserName;

    /** 子评论 */
    @TableField(exist = false)
    private List<BlogFrontComment> children;

    /** 子评论数 */
    @TableField(exist = false)
    private Long replyCount;

    /** 是否点赞 */
    @TableField(exist = false)
    private Boolean isLiked;
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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