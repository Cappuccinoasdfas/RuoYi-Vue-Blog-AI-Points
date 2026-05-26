package com.ruoyi.ai_points_product.controller;

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
import com.ruoyi.ai_points_product.domain.AiPointsProduct;
import com.ruoyi.ai_points_product.service.IAiPointsProductService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分商品Controller
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/ai_points_product/product")
public class AiPointsProductController extends BaseController
{
    @Autowired
    private IAiPointsProductService aiPointsProductService;

    /**
     * 查询积分商品列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(AiPointsProduct aiPointsProduct)
    {
        startPage();
        List<AiPointsProduct> list = aiPointsProductService.selectAiPointsProductList(aiPointsProduct);
        return getDataTable(list);
    }

    /**
     * 导出积分商品列表
     */
    @PreAuthorize("@ss.hasPermi('ai_points_product:product:export')")
    @Log(title = "积分商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiPointsProduct aiPointsProduct)
    {
        List<AiPointsProduct> list = aiPointsProductService.selectAiPointsProductList(aiPointsProduct);
        ExcelUtil<AiPointsProduct> util = new ExcelUtil<AiPointsProduct>(AiPointsProduct.class);
        util.exportExcel(response, list, "积分商品数据");
    }

    /**
     * 获取积分商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai_points_product:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        return success(aiPointsProductService.selectAiPointsProductByProductId(productId));
    }

    /**
     * 新增积分商品
     */
    @PreAuthorize("@ss.hasPermi('ai_points_product:product:add')")
    @Log(title = "积分商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiPointsProduct aiPointsProduct)
    {
        return toAjax(aiPointsProductService.insertAiPointsProduct(aiPointsProduct));
    }

    /**
     * 修改积分商品
     */
    @PreAuthorize("@ss.hasPermi('ai_points_product:product:edit')")
    @Log(title = "积分商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiPointsProduct aiPointsProduct)
    {
        return toAjax(aiPointsProductService.updateAiPointsProduct(aiPointsProduct));
    }

    /**
     * 删除积分商品
     */
    @PreAuthorize("@ss.hasPermi('ai_points_product:product:remove')")
    @Log(title = "积分商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(aiPointsProductService.deleteAiPointsProductByProductIds(productIds));
    }
}
