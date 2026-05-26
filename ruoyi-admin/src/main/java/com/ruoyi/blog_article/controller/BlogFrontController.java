package com.ruoyi.blog_article.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.blog_article.service.IBlogFrontService;
import com.ruoyi.blog_article.domain.BlogFrontVO;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.utils.BlogUrlUtils;
import com.ruoyi.utils.XssUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * 博客文章Controller
 *
 * @author yourname
 * @date 2026-04-09
 */
@Slf4j
@RestController
@RequestMapping("/blog/article")
@RequiredArgsConstructor
public class BlogFrontController {
    //服务器域名用来显示图片
    @Value("${ruoyi.domain}")
    private String domain;
    private final IBlogFrontService iBlogFrontService;
    private final BlogUrlUtils blogUrlUtils;
    private final XssUtils xssUtils;
    // ==================== 公开接口（无需登录） ====================

    /**
     * 查询博客文章列表（分页）- 公开访问
     *
     * @param pageNum 当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param category 分类筛选（可选）
     * @return 分页数据
     */
    @GetMapping("/list")
    @Anonymous
    public AjaxResult list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String category  )//分类筛选
    {
       String status = "0"; //写死只显示发布的文章

        IPage<BlogFrontVO> page = iBlogFrontService.getArticlePage(pageNum, pageSize, category, status);

        // 使用若依的 AjaxResult
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", page.getRecords());
        ajax.put("total", page.getTotal());
        //返回父评论的子评论个数
        page.getRecords().forEach(article -> {});
        return ajax;
    }

    /**
     * 获取文章详情（自增浏览量）- 公开访问
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/detail/{id}")
    @Anonymous
    public AjaxResult getDetail(@PathVariable Long id) {
        // 增加浏览量
        iBlogFrontService.incrementViewCount(id);
        // 获取文章详情（包含作者信息）
        BlogFrontVO article = iBlogFrontService.getArticleWithAuthor(id);

        if (article == null) {
            return AjaxResult.error("文章不存在或已删除");
        }

        return AjaxResult.success(article);
    }

    // ==================== 需要登录的接口 ====================

    /**
     * 获取当前用户的文章列表（包括草稿）
     *
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param status 文章状态（0发布.1草稿）
     * @return 分页数据
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult getMyArticles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Long userId = SecurityUtils.getUserId();
        IPage<BlogFrontVO> page = iBlogFrontService.getUserArticles(userId, pageNum, pageSize, status);

        // 使用若依的 AjaxResult
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", page.getRecords());
        ajax.put("total", page.getTotal());
        return ajax;
    }

    //数据统计
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
        public AjaxResult getStatistics() {
        Map<String, Long> statistics = iBlogFrontService.getStatistics(SecurityUtils.getUserId());
        return AjaxResult.success(statistics);
    }
    /**
     * 发布/保存文章
     */
    @PostMapping("/save")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult save(@Validated @RequestBody BlogFrontVO article) {
        Long userId = SecurityUtils.getUserId();
        //转义XSS
        xssUtils.escape(article);
        boolean result = iBlogFrontService.saveArticle(article,userId);
        return result ? AjaxResult.success("保存成功") : AjaxResult.error("保存失败");
    }

    /**
     * 更新文章
     */
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult update(@Validated @RequestBody BlogFrontVO article) {
        Long userId = SecurityUtils.getUserId(); //通过若依给的方法获取当前登录用户的id
        //转义XSS
        xssUtils.escape(article);
        BlogFrontVO existArticle = iBlogFrontService.getById(article.getId()); //

        if (existArticle == null) return AjaxResult.error("文章不存在");
        if (!existArticle.getUserId().equals(userId)) return AjaxResult.error("无权修改他人文章");
        //  保存前提取相对路径
        article.setCover(blogUrlUtils.extractRelativePath(article.getCover()));
        boolean result = iBlogFrontService.updateArticle(article);
        return result ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
    }

    /**
     * 删除文章（逻辑删除）
     */
    @DeleteMapping("/delete/{ids}")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult delete(@PathVariable Long[] ids) {
        Long userId = SecurityUtils.getUserId();
        boolean result = iBlogFrontService.deleteArticles(ids, userId);
        return result ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 点赞/取消点赞文章
     */
    @PutMapping("/toggleLike/{id}")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult like(@PathVariable Long id) {
        boolean result = iBlogFrontService.toggleLike(id);
        return result ? AjaxResult.success("操作成功") : AjaxResult.error("操作失败");
    }

    //
    /**
     * 上传文章图片
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("读取到的 domain: " + domain);

        try {
            String relativePath = iBlogFrontService.uploadImage(file);
            String fullUrl = domain + relativePath;

            return AjaxResult.success("上传成功", fullUrl);
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

}
