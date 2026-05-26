package com.ruoyi.ai_chat.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IAiChatService {
    //SEE连接//加锁操作
    SseEmitter chatStream(Long userId, String question, Long sessionId);
}
