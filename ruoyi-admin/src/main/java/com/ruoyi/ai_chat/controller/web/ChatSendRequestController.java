package com.ruoyi.ai_chat.controller.web;
import com.ruoyi.ai_chat.domain.ChatRequest;
import com.ruoyi.ai_chat.service.IAiChatService;
import com.ruoyi.ai_chat.service.IAiChatSessionService;
import com.ruoyi.ai_chat.service.IAiPersonalityService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Description: 聊天发送请求
 * @Author: zhongxinghong
 * @Date: 2023/4/20 16:05
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatSendRequestController {  // 类名可以不改，但建议改
    private final IAiPersonalityService aiPersonalityService;
    private final IAiChatSessionService aiChatSessionService;
    private final IAiChatSessionService.IAiChatMessageService aiChatMessageService;
    private final IAiChatService iAiChatService;
    // 用来获取流式数据
    @PostMapping("/stream")
    @Anonymous
    public SseEmitter sendStream(@RequestBody ChatRequest request) {
        Long userId = SecurityUtils.getUserId();
        //加载用户选择的性格
        aiPersonalityService.loadPersonalityToRedis(userId);
        return iAiChatService.chatStream(userId, request.getQuestion(), request.getSessionId());
    }

    //加载会话窗口用来显示在左边的点进去后续可以查询关联的聊天记录
    @GetMapping("/loadSession")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult loadSession(){
        Long userID = SecurityUtils.getUserId();
        return AjaxResult.success(aiChatSessionService.getSession(userID));
    }

    //点击会话请求返回对应的会话id和相关联的聊天记录
    @GetMapping("/getSession")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult getSessionChat(@RequestParam Long sessionId){
        Long userID = SecurityUtils.getUserId();
        //获取对应的会话记录
        return AjaxResult.success(aiChatMessageService.getSessionChat(sessionId, userID));
    }

    // 新增：删除会话
    @DeleteMapping("/deleteSession/{sessionId}")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult deleteSession(@PathVariable Long sessionId){
        Long userID = SecurityUtils.getUserId();
        try {
            boolean result = aiChatSessionService.softDeleteSession(sessionId, userID);
            if (result) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败，会话不存在或无权限");
            }
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }
}
