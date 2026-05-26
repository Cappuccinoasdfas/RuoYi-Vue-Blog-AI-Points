package com.ruoyi.ai_points_product.service.impl;

import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ai_points_product.domain.AiPointsProduct;
import com.ruoyi.ai_points_product.mapper.AiPointsProductMapper;
import com.ruoyi.ai_user_account.service.IAiUserAccountService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_points_product.mapper.AiUserPurchaseMapper;
import com.ruoyi.ai_points_product.domain.AiUserPurchase;
import com.ruoyi.ai_points_product.service.IAiUserPurchaseService;

/**
 * 用户购买记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Service
@RequiredArgsConstructor
public class AiUserPurchaseServiceImpl extends ServiceImpl<AiUserPurchaseMapper, AiUserPurchase> implements IAiUserPurchaseService
{
    private final AiUserPurchaseMapper aiUserPurchaseMapper;
    private final AiPointsProductMapper productMapper;

    /**
     * 查询用户购买记录
     * 
     * @param purchaseId 用户购买记录主键
     * @return 用户购买记录
     */
    @Override
    public AiUserPurchase selectAiUserPurchaseByPurchaseId(Long purchaseId)
    {
        return aiUserPurchaseMapper.selectAiUserPurchaseByPurchaseId(purchaseId);
    }

    /**
     * 查询用户购买记录列表
     * 
     * @param aiUserPurchase 用户购买记录
     * @return 用户购买记录
     */
    @Override
    public List<AiUserPurchase> selectAiUserPurchaseList(AiUserPurchase aiUserPurchase)
    {
        return aiUserPurchaseMapper.selectAiUserPurchaseList(aiUserPurchase);
    }

    /**
     * 新增用户购买记录
     * 
     * @param aiUserPurchase 用户购买记录
     * @return 结果
     */
    @Override
    public int insertAiUserPurchase(AiUserPurchase aiUserPurchase)
    {
        aiUserPurchase.setCreateTime(DateUtils.getNowDate());
        return aiUserPurchaseMapper.insertAiUserPurchase(aiUserPurchase);
    }

    /**
     * 修改用户购买记录
     * 
     * @param aiUserPurchase 用户购买记录
     * @return 结果
     */
    @Override
    public int updateAiUserPurchase(AiUserPurchase aiUserPurchase)
    {
        return aiUserPurchaseMapper.updateAiUserPurchase(aiUserPurchase);
    }

    /**
     * 批量删除用户购买记录
     * 
     * @param purchaseIds 需要删除的用户购买记录主键
     * @return 结果
     */
    @Override
    public int deleteAiUserPurchaseByPurchaseIds(Long[] purchaseIds)
    {
        return aiUserPurchaseMapper.deleteAiUserPurchaseByPurchaseIds(purchaseIds);
    }

    /**
     * 删除用户购买记录信息
     * 
     * @param purchaseId 用户购买记录主键
     * @return 结果
     */
    @Override
    public int deleteAiUserPurchaseByPurchaseId(Long purchaseId)
    {
        return aiUserPurchaseMapper.deleteAiUserPurchaseByPurchaseId(purchaseId);
    }
    /**
     * 查询当前用户买的商品
     *
     * @return 积分商品集合
     */
    @Override
    public List<AiUserPurchase> selectAiPointsProductListByUserId() {
        Long userId = 2L;

        QueryWrapper<AiUserPurchase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time");

        List<AiUserPurchase> list = list(queryWrapper);

        // 返回空集合而不是 null，便于前端处理
        return list == null ? Collections.emptyList() : list;
    }
    /**
     * 判断当前用户是否已购买该商品
     *
     * @param productId 商品id
     * @param userId 用户id
     * @return true 已购买 false 未购买
     */

    @Override
    public boolean isPurchased(Long productId, Long userId) {
        QueryWrapper<AiUserPurchase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("product_id", productId);
        return count(queryWrapper) > 0;
    }

    /**
     * 插入用户购买记录
     *
     * @param productId 商品id
     * @param userId 用户id
     * @return 插入结果
     */
    @Override
    public boolean insertAiUserPurchase(Long productId, Long userId) {
        AiUserPurchase aiUserPurchase = new AiUserPurchase();
        aiUserPurchase.setUserId(userId);
        aiUserPurchase.setProductId(productId);

        // ✅ 加上实际扣的积分
        AiPointsProduct product = productMapper.selectAiPointsProductByProductId(productId);
        aiUserPurchase.setPointsCost(product.getPrice());

        aiUserPurchase.setCreateTime(DateUtils.getNowDate());
        aiUserPurchase.setUpdateTime(DateUtils.getNowDate());
        return save(aiUserPurchase);
    }

}
