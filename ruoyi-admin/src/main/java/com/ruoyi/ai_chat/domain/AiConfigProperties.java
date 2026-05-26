package com.ruoyi.ai_chat.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI聊天服务配置属性
 * 对应 application-ai.yml 中的配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfigProperties {

    private Python python = new Python();
    private Cache cache = new Cache();
    private Lock lock = new Lock();
    private Sse sse = new Sse();

    @Data
    public static class Python {
        /** Python服务URL */
        private String url = "http://127.0.0.1:5000/chat/stream";
        /** 连接超时（毫秒） */
        private int connectTimeout = 5000;
        /** 读取超时（毫秒） */
        private int readTimeout = 120000;
    }

    @Data
    public static class Cache {
        /** 性格配置缓存时间（小时） */
        private int personalityTtlHours = 24;
        /** 聊天记录缓存时间（小时） */
        private int chatHistoryTtlHours = 24;
    }

    @Data
    public static class Lock {
        /** 等待获取锁的时间（秒） */
        private int waitTime = 3;
        /** 锁持有时间（秒） */
        private int leaseTime = 30;
    }

    @Data
    public static class Sse {
        /** SSE连接超时（毫秒） */
        private long timeout = 120000L;
    }
}