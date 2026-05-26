package com.ruoyi.blog_comment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.blog_comment.domain.BlogFrontComment;

import java.util.List;

public interface IBlogFrontCommentService extends IService<BlogFrontComment> {

    /**
     * 获取评论分页列表（包含子评论）
     */
    IPage<BlogFrontComment> getCommentPage(Integer pageNum, Integer pageSize, Long articleId);
    /**
     * 查看总评论数
     */
    Long getTotalCommentCount(Long articleId);

    /**
     * 添加评论
     */
    BlogFrontComment addComment(BlogFrontComment comment);

    /**
     * 获取更多子评论
     */
    List<BlogFrontComment> getMoreChildComments(String parentId, Integer pageNum, Integer pageSize);

    /**
     * 点赞评论
     */
    boolean likeComment(String commentId);

    /**
     * 删除评论（软删除）
     */
    boolean deleteComment(String commentId, Long userId);

    /**
     * 更新文章的评论总数
     * @param articleId 文章ID
     */
    void updateArticleCommentCount(Long articleId);
}