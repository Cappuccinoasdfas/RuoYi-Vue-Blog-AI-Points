package com.ruoyi.ai_points_product.service.transaction;

import com.ruoyi.ai_points_product.service.IAiPointsProductService;
import com.ruoyi.ai_points_product.service.IAiUserPurchaseService;
import com.ruoyi.ai_user_account.service.IAiUserAccountService;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

// ✅ Service：专门负责事务编排
@Service
@RequiredArgsConstructor
public class PurchaseTransactionService {

    private final IAiUserPurchaseService purchaseService;
    private final IAiUserAccountService accountService;
    private final IAiPointsProductService productService;
    private final RedissonClient redissonClient;

    @Transactional
    public AjaxResult executePurchaseTransaction(Long userId, Long productId) {
        String lockKey = "lock:purchase:" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                return AjaxResult.error("操作太频繁，请稍后再试");
            }

            // 1. 检查重复购买
            if (purchaseService.isPurchased(productId, userId)) {
                return AjaxResult.error("已购买过");
            }

            // 2. 查价格
            Long price = productService.getProductPrice(productId);

            // 3. 扣积分（内部已处理账户不存在和余额检查）
            if (!accountService.deductUserPoints(price, userId)) {
                return AjaxResult.error("积分不足");
            }

            // 4. 记录购买
            purchaseService.insertAiUserPurchase(productId, userId);

            return AjaxResult.success("购买成功");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return AjaxResult.error("系统繁忙，请稍后再试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}