package com.ruoyi.blog_article.domain.mail;

import lombok.Builder;
import lombok.Data;
import java.time.Year;
import java.util.Map;

/**
 * 验证码邮件模板变量
 * 用于封装发送验证码邮件时需要的所有模板变量
 */
@Data
@Builder
public class MailVerifyCode {

    /** 验证码 */
    private final String code;

    /** 用户名 */
    private final String username;

    /** 应用名称 */
    private final String appName;

    /** 验证码有效期（分钟） */
    private final int expireMinutes;

    /** 当前年份（用于版权信息） */
    @Builder.Default
    private final int year = Year.now().getValue();

    /**
     * 获取邮件主题
     * 格式：【应用名称】您的验证码
     */
    public String getSubject() {
        return String.format("【%s】您的验证码", appName);
    }

    /**
     * 工厂方法：创建验证码变量对象
     *
     * @param code          验证码
     * @param username      用户名
     * @param appName       应用名称
     * @param expireMinutes 有效期（分钟）
     */
    public static MailVerifyCode of(String code, String username,
                                    String appName, int expireMinutes) {
        return MailVerifyCode.builder()
                .code(code)
                .username(username)
                .appName(appName)
                .expireMinutes(expireMinutes)
                .build();  // year 会自动使用当前年份
    }

    /**
     * 转换为模板变量 Map
     * 方便直接设置到 Thymeleaf Context 中
     */
    public Map<String, Object> toMap() {
        return Map.of(
                "code", code,
                "username", username,
                "appName", appName,
                "subject", getSubject(),
                "expireMinutes", expireMinutes,
                "year", year
        );
    }
}