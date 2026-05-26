package com.ruoyi.blog_article.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@TableName("blog_article")
public class BlogFrontVO {

    /** 文章ID */
    private String id;

    /** 文章标题 */
    @NotBlank(message = "文章标题不能为空")
    @Length(min = 1, max = 20, message = "文章标题长度必须在1-20字符之间")
    private String title;

    /** 文章摘要 */
    @Length(max = 50, message = "文章摘要长度不能超过500字符")
    private String summary;

    /** 文章内容 */
    @NotBlank(message = "文章内容不能为空")
    @Length(min = 10, max = 10000, message = "文章内容长度必须在10-100000字符之间")
    private String content;

    /** 封面图片 */
    @Pattern(regexp = "^(http://|https://|/upload/).*", message = "封面图片格式不正确")
    private String cover;

    /** 分类 */
    @NotBlank(message = "请选择文章分类")
    @Pattern(regexp = "^(frontend|backend|life)$", message = "分类只能是前端/后端/生活")
    private String category;

    /** 分类名称 */
    private String categoryName;

    /** 作者ID */
    private Long userId;

    /** 状态 */
    @Pattern(regexp = "^(0|1)$", message = "状态只能是发布或草稿")
    private String status;

    /** 浏览量 */
    private Long viewCount;

    /** 点赞数 */
    private Long likeCount;

    /** 评论数 */
    private Long commentCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime  updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String authorName;
    @TableField(exist = false)
    private String authorAvatar;
}
