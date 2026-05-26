package com.ruoyi.blog_article.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.blog_article.domain.BlogFrontVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IBlogFrontService  extends IService<BlogFrontVO> {
    /**
     * 点赞/取消点赞
     * @param id 文章id
     */
    boolean toggleLike(Long id);

    /**
     * 增加浏览量
     * @param id 文章id
     */
    void incrementViewCount(Long id);
    /**
     * 更新文章
     * @param article 文章
     */
    boolean updateArticle(BlogFrontVO article);
    /**
     * 保存文章
     * @param article 文章
     * @param userId 用户id
     */

    boolean saveArticle(BlogFrontVO article,Long userId);



    /**
     * 获取文章分页列表（公开）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param category 分类
     * @param status 状态
     */
    IPage<BlogFrontVO> getArticlePage(Integer pageNum, Integer pageSize, String category, String status);

    /**
     * 删除文章（逻辑删除）
     * @param ids 文章id
     * @param userId 用户id
     */
    boolean deleteArticles(Long[] ids, Long userId);

    /**
     * 上传图片
     * @param file 图片文件
     */
    String uploadImage(MultipartFile file);

    /**
     * 获取用户文章列表
     * @param userId 用户id
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 状态
     */
    IPage<BlogFrontVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize, String status);

    /**
     * 获取文章详情（包含作者信息）
     * @param id 文章id
     */
    BlogFrontVO getArticleWithAuthor(Long id);

    /**
     *统计数据
     * @param id 文章id
     */
    Map<String, Long> getStatistics(Long id);


}
