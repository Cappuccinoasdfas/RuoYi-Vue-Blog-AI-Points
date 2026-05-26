package com.ruoyi.ai_points_product.controller.web;
import com.ruoyi.ai_points_product.service.IAiUserPurchaseService;
import com.ruoyi.ai_points_product.service.transaction.PurchaseTransactionService;
import com.ruoyi.ai_user_account.service.IAiUserAccountService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
/**
 * @Description: 聊天发送请求
 * @Author: monophthongizing
 * @Date: 2023/4/20 16:05
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {
    private final IAiUserPurchaseService aiUserPurchase;
    private final PurchaseTransactionService purchaseTransactionService;
    private final IAiUserAccountService aiUserAccount;
    //查询当前用户买过的商品
    @GetMapping("/getProducts")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult getProducts(){
        return AjaxResult.success(aiUserPurchase.selectAiPointsProductListByUserId());
    }

    //购买商品
    @PostMapping("/purchase")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult purchase(@RequestBody Long productId) {
        try {
            Long userId = SecurityUtils.getUserId();
            return purchaseTransactionService.executePurchaseTransaction(userId, productId);
        } catch (Exception e) {
            log.error("购买失败", e);
            return AjaxResult.error("系统繁忙，请稍后再试");
        }
    }

    //获取用户当前积分
    @GetMapping("/getPoints")
    @PreAuthorize("isAuthenticated()") //登录后的权限
    public AjaxResult getPoints(){
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(aiUserAccount.selectAiUserAccountBalanceByUserId(userId));
    }

}
