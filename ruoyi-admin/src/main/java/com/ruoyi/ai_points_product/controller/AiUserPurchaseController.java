package com.ruoyi.ai_points_product.controller;

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
import com.ruoyi.ai_points_product.domain.AiUserPurchase;
import com.ruoyi.ai_points_product.service.IAiUserPurchaseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户购买记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/ai_user_purchase/purchase")
public class AiUserPurchaseController extends BaseController
{
    @Autowired
    private IAiUserPurchaseService aiUserPurchaseService;

    /**
     * 查询用户购买记录列表
     */
    @PreAuthorize("@ss.hasPermi('ai_user_purchase:purchase:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiUserPurchase aiUserPurchase)
    {
        startPage();
        List<AiUserPurchase> list = aiUserPurchaseService.selectAiUserPurchaseList(aiUserPurchase);
        return getDataTable(list);
    }

    /**
     * 导出用户购买记录列表
     */
    @PreAuthorize("@ss.hasPermi('ai_user_purchase:purchase:export')")
    @Log(title = "用户购买记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiUserPurchase aiUserPurchase)
    {
        List<AiUserPurchase> list = aiUserPurchaseService.selectAiUserPurchaseList(aiUserPurchase);
        ExcelUtil<AiUserPurchase> util = new ExcelUtil<AiUserPurchase>(AiUserPurchase.class);
        util.exportExcel(response, list, "用户购买记录数据");
    }

    /**
     * 获取用户购买记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai_user_purchase:purchase:query')")
    @GetMapping(value = "/{purchaseId}")
    public AjaxResult getInfo(@PathVariable("purchaseId") Long purchaseId)
    {
        return success(aiUserPurchaseService.selectAiUserPurchaseByPurchaseId(purchaseId));
    }

    /**
     * 新增用户购买记录
     */
    @PreAuthorize("@ss.hasPermi('ai_user_purchase:purchase:add')")
    @Log(title = "用户购买记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiUserPurchase aiUserPurchase)
    {
        return toAjax(aiUserPurchaseService.insertAiUserPurchase(aiUserPurchase));
    }

    /**
     * 修改用户购买记录
     */
    @PreAuthorize("@ss.hasPermi('ai_user_purchase:purchase:edit')")
    @Log(title = "用户购买记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiUserPurchase aiUserPurchase)
    {
        return toAjax(aiUserPurchaseService.updateAiUserPurchase(aiUserPurchase));
    }

    /**
     * 删除用户购买记录
     */
    @PreAuthorize("@ss.hasPermi('ai_user_purchase:purchase:remove')")
    @Log(title = "用户购买记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{purchaseIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseIds)
    {
        return toAjax(aiUserPurchaseService.deleteAiUserPurchaseByPurchaseIds(purchaseIds));
    }
}
