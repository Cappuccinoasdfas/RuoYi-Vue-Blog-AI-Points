package com.ruoyi.blog_article.domain.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailContext {
    /** 收件人邮箱 */
    private String to;
    /** 邮件主题 */
    private String subject;
    /** 模板名称（不含后缀） */
    private String templateName;
    /** 模板变量 */
    private Object variables;
}