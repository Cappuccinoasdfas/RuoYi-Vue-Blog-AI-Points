package com.ruoyi.blog_article.service;

import com.ruoyi.blog_article.domain.mail.MailContext;

import java.time.LocalDateTime;

public interface IMailServiceService {
    /**
     * 发送模板邮件
     */
    void send(MailContext context);

    /**
     * 发送注册验证码
     */
    void sendVerificationCode(String to, String username);
    /**
     * 发送违规封禁提示
     */
     void sendBlocked(String to,String description, String username);
    /**
     * 判断用户是否注册过这个邮箱
     */
    Boolean getEmail(String to);
    }
