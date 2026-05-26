package com.ruoyi.utils;

import com.ruoyi.blog_article.domain.BlogFrontVO;
import org.springframework.stereotype.Component;

@Component  // 确保能被注入
public class XssUtils {

    /**
     * 转义单个字符串
     */
    public String escape(String input) {
        if (input == null) return null;
        return input
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * 转义整个 BlogFrontVO 对象
     */
    public void escape(BlogFrontVO article) {
        if (article == null) return;

        article.setTitle(escape(article.getTitle()));
        article.setSummary(escape(article.getSummary()));
        article.setContent(escape(article.getContent()));
        article.setCategory(escape(article.getCategory()));
        article.setCategoryName(escape(article.getCategoryName()));
    }
}