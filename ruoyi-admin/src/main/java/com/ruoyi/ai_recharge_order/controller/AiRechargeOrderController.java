package com.ruoyi.ai_recharge_order.controller;

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
import com.ruoyi.ai_recharge_order.domain.AiRechargeOrder;
import com.ruoyi.ai_recharge_order.service.IAiRechargeOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 充值订单Controller
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/ai_recharge_order/order")
public class AiRechargeOrderController extends BaseController
{
    @Autowired
    private IAiRechargeOrderService aiRechargeOrderService;

    /**
     * 查询充值订单列表
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_order:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiRechargeOrder aiRechargeOrder)
    {
        startPage();
        List<AiRechargeOrder> list = aiRechargeOrderService.selectAiRechargeOrderList(aiRechargeOrder);
        return getDataTable(list);
    }

    /**
     * 导出充值订单列表
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_order:order:export')")
    @Log(title = "充值订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiRechargeOrder aiRechargeOrder)
    {
        List<AiRechargeOrder> list = aiRechargeOrderService.selectAiRechargeOrderList(aiRechargeOrder);
        ExcelUtil<AiRechargeOrder> util = new ExcelUtil<AiRechargeOrder>(AiRechargeOrder.class);
        util.exportExcel(response, list, "充值订单数据");
    }

    /**
     * 获取充值订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_order:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(aiRechargeOrderService.selectAiRechargeOrderByOrderId(orderId));
    }

    /**
     * 新增充值订单
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_order:order:add')")
    @Log(title = "充值订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiRechargeOrder aiRechargeOrder)
    {
        return toAjax(aiRechargeOrderService.insertAiRechargeOrder(aiRechargeOrder));
    }

    /**
     * 修改充值订单
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_order:order:edit')")
    @Log(title = "充值订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiRechargeOrder aiRechargeOrder)
    {
        return toAjax(aiRechargeOrderService.updateAiRechargeOrder(aiRechargeOrder));
    }

    /**
     * 删除充值订单
     */
    @PreAuthorize("@ss.hasPermi('ai_recharge_order:order:remove')")
    @Log(title = "充值订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(aiRechargeOrderService.deleteAiRechargeOrderByOrderIds(orderIds));
    }
}
