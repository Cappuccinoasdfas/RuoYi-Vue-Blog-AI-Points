package com.ruoyi.ai_chat.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.ai_chat.domain.AiChatSession;
import com.ruoyi.ai_chat.service.IAiChatSessionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI聊天会话Controller
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/ai_chat_session/session")
public class AiChatSessionController extends BaseController
{
    @Autowired
    private IAiChatSessionService aiChatSessionService;

    /**
     * 查询AI聊天会话列表
     */
    @PreAuthorize("@ss.hasPermi('ai_chat_session:session:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiChatSession aiChatSession)
    {
        startPage();
        List<AiChatSession> list = aiChatSessionService.selectAiChatSessionList(aiChatSession);
        return getDataTable(list);
    }

    /**
     * 导出AI聊天会话列表
     */
    @PreAuthorize("@ss.hasPermi('ai_chat_session:session:export')")
    @Log(title = "AI聊天会话", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiChatSession aiChatSession)
    {
        List<AiChatSession> list = aiChatSessionService.selectAiChatSessionList(aiChatSession);
        ExcelUtil<AiChatSession> util = new ExcelUtil<AiChatSession>(AiChatSession.class);
        util.exportExcel(response, list, "AI聊天会话数据");
    }

    /**
     * 获取AI聊天会话详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai_chat_session:session:query')")
    @GetMapping(value = "/{sessionId}")
    public AjaxResult getInfo(@PathVariable("sessionId") Long sessionId)
    {
        return success(aiChatSessionService.selectAiChatSessionBySessionId(sessionId));
    }

    /**
     * 新增AI聊天会话
     */
    @PreAuthorize("@ss.hasPermi('ai_chat_session:session:add')")
    @Log(title = "AI聊天会话", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiChatSession aiChatSession)
    {
        return toAjax(aiChatSessionService.insertAiChatSession(aiChatSession));
    }

    /**
     * 修改AI聊天会话
     */
    @PreAuthorize("@ss.hasPermi('ai_chat_session:session:edit')")
    @Log(title = "AI聊天会话", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiChatSession aiChatSession)
    {
        return toAjax(aiChatSessionService.updateAiChatSession(aiChatSession));
    }

    /**
     * 删除AI聊天会话
     */
    @PreAuthorize("@ss.hasPermi('ai_chat_session:session:remove')")
    @Log(title = "AI聊天会话", businessType = BusinessType.DELETE)
	@DeleteMapping("/{sessionIds}")
    public AjaxResult remove(@PathVariable Long[] sessionIds)
    {
        return toAjax(aiChatSessionService.deleteAiChatSessionBySessionIds(sessionIds));
    }
}
