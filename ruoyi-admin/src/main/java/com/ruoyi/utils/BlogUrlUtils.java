package com.ruoyi.utils;

import com.ruoyi.blog_article.domain.BlogArticle;
import com.ruoyi.blog_article.domain.BlogFrontVO;
import com.ruoyi.blog_comment.domain.BlogFrontComment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class BlogUrlUtils {
    @Value("${ruoyi.domain}")
    private String domain;
    /**
     * 统一处理 URL 拼接
     */
    public void processUrls(BlogFrontVO article) {
        if (article == null) return;

        // 处理封面图
        if (article.getCover() != null && !article.getCover().startsWith("http")) {
            article.setCover(domain + article.getCover());
        }

        // 处理作者头像
        if (article.getAuthorAvatar() != null && !article.getAuthorAvatar().startsWith("http")) {
            article.setAuthorAvatar(domain + article.getAuthorAvatar());
        }
    }
    /**
     * 统一处理 URL 拼接
     */
    public void processUrlsMB(BlogArticle article) {
        if (article == null) return;

        // 处理封面图
        if (article.getCover() != null && !article.getCover().startsWith("http")) {
            article.setCover(domain + article.getCover());
        }

    }
    /**
     * 统一处理 URL 拼接（评论头像）
     */
    public void processCommentUrls(BlogFrontComment comment) {
        if (comment == null) return;

        // 处理用户头像
        if (comment.getUserAvatar() != null && !comment.getUserAvatar().startsWith("http")) {
            comment.setUserAvatar(domain + comment.getUserAvatar());
        }

        // 递归处理子评论
        if (comment.getChildren() != null) {
            for (BlogFrontComment child : comment.getChildren()) {
                processCommentUrls(child);
            }
        }
    }
    /**
     * 从完整 URL 提取相对路径
     */
    public String extractRelativePath(String url) {
        if (url == null || url.isEmpty()) return url;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url.replace(domain, "");  // 直接去掉域名
        }
        return url;
    }
}
