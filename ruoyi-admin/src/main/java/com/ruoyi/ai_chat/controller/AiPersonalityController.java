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
import com.ruoyi.ai_chat.domain.AiPersonality;
import com.ruoyi.ai_chat.service.IAiPersonalityService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI性格Controller
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/ai_personality/personality")
public class AiPersonalityController extends BaseController
{
    @Autowired
    private IAiPersonalityService aiPersonalityService;

    /**
     * 查询AI性格列表
     */
    @PreAuthorize("@ss.hasPermi('ai_personality:personality:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiPersonality aiPersonality)
    {
        startPage();
        List<AiPersonality> list = aiPersonalityService.selectAiPersonalityList(aiPersonality);
        return getDataTable(list);
    }

    /**
     * 导出AI性格列表
     */
    @PreAuthorize("@ss.hasPermi('ai_personality:personality:export')")
    @Log(title = "AI性格", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiPersonality aiPersonality)
    {
        List<AiPersonality> list = aiPersonalityService.selectAiPersonalityList(aiPersonality);
        ExcelUtil<AiPersonality> util = new ExcelUtil<AiPersonality>(AiPersonality.class);
        util.exportExcel(response, list, "AI性格数据");
    }

    /**
     * 获取AI性格详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai_personality:personality:query')")
    @GetMapping(value = "/{personalityId}")
    public AjaxResult getInfo(@PathVariable("personalityId") Long personalityId)
    {
        return success(aiPersonalityService.selectAiPersonalityByPersonalityId(personalityId));
    }

    /**
     * 新增AI性格
     */
    @PreAuthorize("@ss.hasPermi('ai_personality:personality:add')")
    @Log(title = "AI性格", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiPersonality aiPersonality)
    {
        return toAjax(aiPersonalityService.insertAiPersonality(aiPersonality));
    }

    /**
     * 修改AI性格
     */
    @PreAuthorize("@ss.hasPermi('ai_personality:personality:edit')")
    @Log(title = "AI性格", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiPersonality aiPersonality)
    {
        return toAjax(aiPersonalityService.updateAiPersonality(aiPersonality));
    }

    /**
     * 删除AI性格
     */
    @PreAuthorize("@ss.hasPermi('ai_personality:personality:remove')")
    @Log(title = "AI性格", businessType = BusinessType.DELETE)
	@DeleteMapping("/{personalityIds}")
    public AjaxResult remove(@PathVariable Long[] personalityIds)
    {
        return toAjax(aiPersonalityService.deleteAiPersonalityByPersonalityIds(personalityIds));
    }
}
