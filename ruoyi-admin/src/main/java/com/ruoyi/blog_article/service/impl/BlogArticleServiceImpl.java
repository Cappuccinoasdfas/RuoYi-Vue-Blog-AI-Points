package com.ruoyi.blog_article.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.utils.BlogUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blog_article.mapper.BlogArticleMapper;
import com.ruoyi.blog_article.domain.BlogArticle;
import com.ruoyi.blog_article.service.IBlogArticleService;

/**
 * 博客文章Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-07
 */
@Service
public class BlogArticleServiceImpl implements IBlogArticleService {
    @Autowired
    private BlogArticleMapper blogArticleMapper;
    //工具类
    @Autowired
    private BlogUrlUtils blogUrlUtils;
    /**
     * 查询博客文章
     *
     * @param id 博客文章主键
     * @return 博客文章
     */
    @Override
    public BlogArticle selectBlogArticleById(Long id) {
        BlogArticle blogArticle = blogArticleMapper.selectBlogArticleById(id);
        blogUrlUtils.processUrlsMB(blogArticle);
        return blogArticle;
    }


    /**
     * 查询博客文章列表
     *
     * @param blogArticle 博客文章
     * @return 博客文章
     */
    @Override
    public List<BlogArticle> selectBlogArticleList(BlogArticle blogArticle) {
        List<BlogArticle> blogArticles = blogArticleMapper.selectBlogArticleList(blogArticle);
        blogArticles.forEach(blogUrlUtils::processUrlsMB);
        return blogArticles;
    }

    /**
     * 新增博客文章
     * 
     * @param blogArticle 博客文章
     * @return 结果
     */
    @Override
    public int insertBlogArticle(BlogArticle blogArticle) {
        blogArticle.setCreateTime(DateUtils.getNowDate());
        return blogArticleMapper.insertBlogArticle(blogArticle);
    }

    /**
     * 修改博客文章
     *
     * @param blogArticle 博客文章
     * @return 结果
     */
    @Override
    public int updateBlogArticle(BlogArticle blogArticle) {
        blogArticle.setUpdateTime(DateUtils.getNowDate());
        return blogArticleMapper.updateBlogArticle(blogArticle);
    }

    /**
     * 批量删除博客文章
     *
     * @param ids 需要删除的博客文章主键
     * @return 结果
     */
    @Override
    public int deleteBlogArticleByIds(Long[] ids) {
        return blogArticleMapper.deleteBlogArticleByIds(ids);
    }

    /**
     * 删除博客文章信息
     *
     * @param id 博客文章主键
     * @return 结果
     */
    @Override
    public int deleteBlogArticleById(Long id) {
        return blogArticleMapper.deleteBlogArticleById(id);
    }

 }
