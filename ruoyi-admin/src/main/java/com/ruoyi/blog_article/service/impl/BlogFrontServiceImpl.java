package com.ruoyi.blog_article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.blog_article.domain.BlogFrontVO;
import com.ruoyi.blog_article.mapper.BlogFrontMapper;
import com.ruoyi.blog_article.service.IBlogFrontService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.utils.BlogUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlogFrontServiceImpl  extends ServiceImpl<BlogFrontMapper, BlogFrontVO> implements IBlogFrontService {
    @Autowired
    private BlogFrontMapper blogFrontServicel;
    //工具类
    @Autowired
    private BlogUrlUtils blogUrlUtils;
    @Autowired
    private BlogFrontMapper blogFrontMapper;
    /**
     * 获取文章分页列表（公开）
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param category 分类
     * @param status 状态
     * @ return
     */
    @Override
    public IPage<BlogFrontVO> getArticlePage(Integer pageNum, Integer pageSize, String category, String status) {
        //
        Page<BlogFrontVO> page = new Page<>(pageNum, pageSize);
        IPage<BlogFrontVO> result = blogFrontServicel.selectArticlePage(page, status, category);
        result.getRecords().forEach(blogUrlUtils::processUrls);
        return result;
    }

    /**
     * 获取文章详情（包含作者信息）
     */
    @Override
    public BlogFrontVO getArticleWithAuthor(Long id) {


        BlogFrontVO article = blogFrontServicel.selectArticleWithAuthor(id);
        //  先判空
        if (article == null) {
            return null;
        }
        //判断当前文章是否变成草稿
        if("1".equals(article.getStatus())){
            return null;
        }
        blogUrlUtils.processUrls(article);

        return article;
    }

    /**
     * 统计数据
     */
    @Override
    public Map<String, Long> getStatistics(Long id) {
        // 调用 Mapper 层自定义方法，一次性查询所有统计数据
        // 返回的 Map 中 key 是字段别名，value 是统计结果（默认是 BigDecimal 或 Long 类型）
        Map<String, Object> result = blogFrontMapper.selectStatistics(id);

        // 创建返回结果的 Map（使用 Long 类型保证数据精度）
        Map<String, Long> statistics = new HashMap<>();

        // 批量处理
        Map.of(
                "count", "total_count",
                "viewCount", "total_view",
                "likeCount", "total_like",
                "draftCount", "draft_count"
        )
        .forEach((key, dbKey) ->
                statistics.put(key, ((Number) result.getOrDefault(dbKey, 0)).longValue())
        );

        return statistics;
    }

    /**
     * 获取用户文章列表
     */
    @Override
    public IPage<BlogFrontVO> getUserArticles(Long userId, Integer pageNum,
                                              Integer pageSize, String status) {
        Page<BlogFrontVO> page = new Page<>(pageNum, pageSize);
        IPage<BlogFrontVO> blogArticleMPIPage = blogFrontServicel.selectUserArticles(page, userId, status);
        blogArticleMPIPage.getRecords().forEach(blogUrlUtils::processUrls);
        return blogArticleMPIPage;
    }

    /**
     * 保存文章
     */
    @Override
    @Transactional
    public boolean saveArticle(BlogFrontVO article,Long userId) {
        article.setUserId(userId);

        article.setCover(blogUrlUtils.extractRelativePath(article.getCover()));
        //设置默认值
        article.setViewCount(0L);
        article.setLikeCount(0L);
        article.setCommentCount(0L);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        return this.save(article);
    }

    /**
     * 更新文章
     */
    @Override
    @Transactional
    public boolean updateArticle(BlogFrontVO article) {
        article.setUpdateTime(LocalDateTime.now());
        return this.updateById(article);
    }

    /**
     * 删除文章（逻辑删除）
     */
    @Override
    @Transactional
    public boolean deleteArticles(Long[] ids, Long userId) {
        LambdaQueryWrapper<BlogFrontVO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogFrontVO::getId, ids)
                .eq(BlogFrontVO::getUserId, userId); // 只能删除自己的文章

        return this.remove(wrapper);
    }

    /**
     * 增加浏览量
     */
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        BlogFrontVO article = this.getById(id); //查找id为多少的文章信息
        if (article != null) {//如果不 为空
            Long currentCount = article.getViewCount();
            article.setViewCount(currentCount + 1);//浏览量+1
            this.updateById(article);
        }
    }

    /**
     * 点赞
     */
    @Override
    @Transactional
    public boolean toggleLike(Long id) {
        BlogFrontVO article = this.getById(id);
        if (article == null) {
            return false;
        }

        // TODO: 需要配合点赞记录表实现真正的点赞/取消点赞
        // 这里简化为直接增加点赞数
        article.setLikeCount(article.getLikeCount() + 1);
        return this.updateById(article);
    }

    /**
     * 上传图片
     * @param file 图片文件
     * @return 图片访问路径
     */
    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 若依自带的文件上传工具
            // 文件会保存到 D:/ruoyi/uploadPath/upload/2026/04/10/xxx.jpg
            String filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);
            //后续逻辑检查同文章下是不是已经有图了有就删除旧图
            //----判断逻辑后续优化做----//
            // 删除文件
//            FileUtils.deleteFile(filePath);
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("图片上传失败：" + e.getMessage());
        }
    }
}
