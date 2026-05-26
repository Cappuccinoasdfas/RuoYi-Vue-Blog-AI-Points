package com.ruoyi.ai_chat.service.python;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.ai_chat.domain.AiConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Python AI服务客户端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PythonChatClient {

    private final AiConfigProperties aiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送流式对话请求
     *
     * @param userId     用户ID
     * @param question   用户问题
     * @param sessionId  会话ID
     * @param onToken    收到token时的回调
     * @param onComplete 完成时的回调
     * @param onError    出错时的回调
     */
    public void sendStreamRequest(Long userId, String question, Long sessionId,
                                  Consumer<String> onToken,
                                  Runnable onComplete,
                                  Consumer<Exception> onError) {
        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        request.put("session_id", sessionId);
        request.put("question", question);

        RestTemplate restTemplate = createRestTemplate();

        try {
            restTemplate.execute(aiConfig.getPython().getUrl(), HttpMethod.POST,
                    req -> {
                        req.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        req.getBody().write(objectMapper.writeValueAsBytes(request));
                    },
                    res -> {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(res.getBody(), StandardCharsets.UTF_8));
                        String line;

                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                String jsonStr = line.substring(6);
                                Map<String, Object> data = objectMapper.readValue(jsonStr, Map.class);
                                String type = (String) data.get("type");

                                if ("token".equals(type)) {
                                    String token = (String) data.get("data");
                                    onToken.accept(token);
                                } else if ("done".equals(type)) {
                                    log.info("收到 done 事件");
                                    onComplete.run();
                                }
                            }
                        }
                        return null;
                    }
            );
        } catch (Exception e) {
            log.error("Python服务调用失败", e);
            onError.accept(e);
        }
    }

    //
    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(aiConfig.getPython().getConnectTimeout());
        factory.setReadTimeout(aiConfig.getPython().getReadTimeout());
        return new RestTemplate(factory);
    }
}