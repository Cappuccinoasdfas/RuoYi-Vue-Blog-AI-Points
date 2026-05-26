package com.ruoyi.ai_user_account.controller;

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
import com.ruoyi.ai_user_account.domain.AiUserAccount;
import com.ruoyi.ai_user_account.service.IAiUserAccountService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户积分账户Controller
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/ai_user_account/account")
public class AiUserAccountController extends BaseController
{
    @Autowired
    private IAiUserAccountService aiUserAccountService;

    /**
     * 查询用户积分账户列表
     */
    @PreAuthorize("@ss.hasPermi('ai_user_account:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiUserAccount aiUserAccount)
    {
        startPage();
        List<AiUserAccount> list = aiUserAccountService.selectAiUserAccountList(aiUserAccount);
        return getDataTable(list);
    }

    /**
     * 导出用户积分账户列表
     */
    @PreAuthorize("@ss.hasPermi('ai_user_account:account:export')")
    @Log(title = "用户积分账户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiUserAccount aiUserAccount)
    {
        List<AiUserAccount> list = aiUserAccountService.selectAiUserAccountList(aiUserAccount);
        ExcelUtil<AiUserAccount> util = new ExcelUtil<AiUserAccount>(AiUserAccount.class);
        util.exportExcel(response, list, "用户积分账户数据");
    }

    /**
     * 获取用户积分账户详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai_user_account:account:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") Long accountId)
    {
        return success(aiUserAccountService.selectAiUserAccountByAccountId(accountId));
    }

    /**
     * 新增用户积分账户
     */
    @PreAuthorize("@ss.hasPermi('ai_user_account:account:add')")
    @Log(title = "用户积分账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiUserAccount aiUserAccount)
    {
        return toAjax(aiUserAccountService.insertAiUserAccount(aiUserAccount));
    }

    /**
     * 修改用户积分账户
     */
    @PreAuthorize("@ss.hasPermi('ai_user_account:account:edit')")
    @Log(title = "用户积分账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiUserAccount aiUserAccount)
    {
        return toAjax(aiUserAccountService.updateAiUserAccount(aiUserAccount));
    }

    /**
     * 删除用户积分账户
     */
    @PreAuthorize("@ss.hasPermi('ai_user_account:account:remove')")
    @Log(title = "用户积分账户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds)
    {
        return toAjax(aiUserAccountService.deleteAiUserAccountByAccountIds(accountIds));
    }
}
