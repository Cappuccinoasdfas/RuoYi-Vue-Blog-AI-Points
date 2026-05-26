package com.ruoyi.blog_article.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 博客文章对象 blog_article
 *
 * @author ruoyi
 * @date 2026-04-07
 */
@Data
@TableName("blog_article")
public class BlogArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文章ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 文章标题 */
    @Excel(name = "文章标题")
    private String title;

    /** 文章摘要 */
    @Excel(name = "文章摘要")
    private String summary;

    /** 文章内容 */
    @Excel(name = "文章内容")
    private String content;

    /** 封面图片 */
    @Excel(name = "封面图片")
    private String cover;

    /** 分类（frontend/backend/life） */
    @Excel(name = "分类", readConverterExp = "f=rontend/backend/life")
    private String category;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 浏览量 */
    @Excel(name = "浏览量")
    private Long viewCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Long commentCount;

    /** 作者ID（关联sys_user） */
    @Excel(name = "作者ID", readConverterExp = "关=联sys_user")
    private Long userId;

    /** 状态（0发布 1草稿） */
    @Excel(name = "状态", readConverterExp = "0=发布,1=草稿")
    private String status;

    /** 删除标志 */
    @TableLogic
    private String delFlag;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("title", getTitle())
                .append("summary", getSummary())
                .append("content", getContent())
                .append("cover", getCover())
                .append("category", getCategory())
                .append("categoryName", getCategoryName())
                .append("viewCount", getViewCount())
                .append("likeCount", getLikeCount())
                .append("commentCount", getCommentCount())
                .append("userId", getUserId())
                .append("status", getStatus())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}