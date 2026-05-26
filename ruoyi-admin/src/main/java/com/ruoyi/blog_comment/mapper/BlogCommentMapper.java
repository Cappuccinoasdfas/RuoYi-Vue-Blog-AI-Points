package com.ruoyi.blog_comment.mapper;

import java.util.List;
import com.ruoyi.blog_comment.domain.BlogComment;

/**
 * 博客评论Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-14
 */
public interface BlogCommentMapper 
{
    /**
     * 查询博客评论
     * 
     * @param id 博客评论主键
     * @return 博客评论
     */
    public BlogComment selectBlogCommentById(Long id);

    /**
     * 查询博客评论列表
     * 
     * @param blogComment 博客评论
     * @return 博客评论集合
     */
    public List<BlogComment> selectBlogCommentList(BlogComment blogComment);

    /**
     * 新增博客评论
     * 
     * @param blogComment 博客评论
     * @return 结果
     */
    public int insertBlogComment(BlogComment blogComment);

    /**
     * 修改博客评论
     * 
     * @param blogComment 博客评论
     * @return 结果
     */
    public int updateBlogComment(BlogComment blogComment);

    /**
     * 删除博客评论
     * 
     * @param id 博客评论主键
     * @return 结果
     */
    public int deleteBlogCommentById(Long id);

    /**
     * 批量删除博客评论
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBlogCommentByIds(Long[] ids);
}
