package com.ruoyi.ai_chat.domain;

import lombok.Data;

@Data
public class ChatRequest {
    //问题
    private String question;
    //会话ID
    private Long sessionId;
}
