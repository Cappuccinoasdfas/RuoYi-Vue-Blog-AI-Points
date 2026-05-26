package com.ruoyi.ai_recharge_package.controller;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
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
import com.ruoyi.ai_recharge_package.domain.AiRechargePackage;
import com.ruoyi.ai_recharge_package.service.IAiRechargePackageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 充值套餐Controller
 * 
 * @author ruoyi
 * @date 2026-04-28
 */
@RestController
@RequestMapping("/ai_recharge_package/package")
public class AiRechargePackageController extends BaseController
{
    @Autowired
    private IAiRechargePackageService aiRechargePackageService;

    /**
     * 查询充值套餐列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(AiRechargePackage aiRechargePackage)
    {
        startPage();
        List<AiRechargePackage> list = aiRechargePackageService.selectAiRechargePackageList(aiRechargePackage);
        return getDataTable(list);
    }

    /**
     * 导出充值套餐列表
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_package:package:export')")
    @Log(title = "充值套餐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiRechargePackage aiRechargePackage)
    {
        List<AiRechargePackage> list = aiRechargePackageService.selectAiRechargePackageList(aiRechargePackage);
        ExcelUtil<AiRechargePackage> util = new ExcelUtil<AiRechargePackage>(AiRechargePackage.class);
        util.exportExcel(response, list, "充值套餐数据");
    }

    /**
     * 获取充值套餐详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_package:package:query')")
    @GetMapping(value = "/{packageId}")
    public AjaxResult getInfo(@PathVariable("packageId") Long packageId)
    {
        return success(aiRechargePackageService.selectAiRechargePackageByPackageId(packageId));
    }

    /**
     * 新增充值套餐
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_package:package:add')")
    @Log(title = "充值套餐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiRechargePackage aiRechargePackage)
    {
        return toAjax(aiRechargePackageService.insertAiRechargePackage(aiRechargePackage));
    }

    /**
     * 修改充值套餐
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_package:package:edit')")
    @Log(title = "充值套餐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiRechargePackage aiRechargePackage)
    {
        return toAjax(aiRechargePackageService.updateAiRechargePackage(aiRechargePackage));
    }

    /**
     * 删除充值套餐
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_package:package:remove')")
    @Log(title = "充值套餐", businessType = BusinessType.DELETE)
	@DeleteMapping("/{packageIds}")
    public AjaxResult remove(@PathVariable Long[] packageIds)
    {
        return toAjax(aiRechargePackageService.deleteAiRechargePackageByPackageIds(packageIds));
    }
}
