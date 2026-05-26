package com.ruoyi.blog_article.domain.mail;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class MailBan {
    /** 用户名 */
    private final String username;

    /** 应用名称 */
    private final String appName;

    /**违规描述*/
    private final String description;
    /** 违规时间 */
    private final LocalDateTime time;
    // 解封时间
    private final LocalDateTime  unblockTime;

    //工厂方法
    public static MailBan of(String username, String appName, String description, LocalDateTime  time, LocalDateTime  unblockTime) {
        return MailBan.builder()
                .username(username)
                .appName(appName)
                .description(description)
                .time(time)
                .unblockTime(unblockTime)
                .build();
    }
    //转为模板变量
    public Map<String, Object> toMap() {
        return Map.of(
                "username", username,
                "appName", appName,
                "description", description,
                "time", time,
                "unblockTime", unblockTime
        );
    }

    public String getSubject() {
        return "【" + appName + "】违规提示";
    }
}
