package com.ruoyi.apiDoc.controller;

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
import com.ruoyi.apiDoc.domain.SysApiDoc;
import com.ruoyi.apiDoc.service.ISysApiDocService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * API接口文档Controller
 * 
 * @author ruoyi
 * @date 2026-04-19
 */
@RestController
@RequestMapping("/apiDoc/doc")
public class SysApiDocController extends BaseController
{
    @Autowired
    private ISysApiDocService sysApiDocService;

    /**
     * 查询API接口文档列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(SysApiDoc sysApiDoc)
    {
        startPage();
        List<SysApiDoc> list = sysApiDocService.selectSysApiDocList(sysApiDoc);
        return getDataTable(list);
    }

    /**
     * 导出API接口文档列表
     */
    @PreAuthorize("@ss.hasPermi('apiDoc:doc:export')")
    @Log(title = "API接口文档", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysApiDoc sysApiDoc)
    {
        List<SysApiDoc> list = sysApiDocService.selectSysApiDocList(sysApiDoc);
        ExcelUtil<SysApiDoc> util = new ExcelUtil<SysApiDoc>(SysApiDoc.class);
        util.exportExcel(response, list, "API接口文档数据");
    }

    /**
     * 获取API接口文档详细信息
     */
    @PreAuthorize("@ss.hasPermi('apiDoc:doc:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysApiDocService.selectSysApiDocById(id));
    }

    /**
     * 新增API接口文档
     */
    @PreAuthorize("@ss.hasPermi('apiDoc:doc:add')")
    @Log(title = "API接口文档", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysApiDoc sysApiDoc)
    {
        return toAjax(sysApiDocService.insertSysApiDoc(sysApiDoc));
    }

    /**
     * 修改API接口文档
     */
    @PreAuthorize("@ss.hasPermi('apiDoc:doc:edit')")
    @Log(title = "API接口文档", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysApiDoc sysApiDoc)
    {
        return toAjax(sysApiDocService.updateSysApiDoc(sysApiDoc));
    }

    /**
     * 删除API接口文档
     */
    @PreAuthorize("@ss.hasPermi('apiDoc:doc:remove')")
    @Log(title = "API接口文档", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysApiDocService.deleteSysApiDocByIds(ids));
    }
}
