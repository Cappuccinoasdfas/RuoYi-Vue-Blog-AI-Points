package com.ruoyi.ai_points_product.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ai_points_product.domain.AiPointsProduct;
import com.ruoyi.ai_points_product.domain.AiUserPurchase;

/**
 * 用户购买记录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
public interface IAiUserPurchaseService extends IService<AiUserPurchase>
{
    /**
     * 查询用户购买记录
     * 
     * @param purchaseId 用户购买记录主键
     * @return 用户购买记录
     */
    public AiUserPurchase selectAiUserPurchaseByPurchaseId(Long purchaseId);

    /**
     * 查询用户购买记录列表
     * 
     * @param aiUserPurchase 用户购买记录
     * @return 用户购买记录集合
     */
    public List<AiUserPurchase> selectAiUserPurchaseList(AiUserPurchase aiUserPurchase);

    /**
     * 新增用户购买记录
     * 
     * @param aiUserPurchase 用户购买记录
     * @return 结果
     */
    public int insertAiUserPurchase(AiUserPurchase aiUserPurchase);

    /**
     * 修改用户购买记录
     * 
     * @param aiUserPurchase 用户购买记录
     * @return 结果
     */
    public int updateAiUserPurchase(AiUserPurchase aiUserPurchase);

    /**
     * 批量删除用户购买记录
     * 
     * @param purchaseIds 需要删除的用户购买记录主键集合
     * @return 结果
     */
    public int deleteAiUserPurchaseByPurchaseIds(Long[] purchaseIds);

    /**
     * 删除用户购买记录信息
     * 
     * @param purchaseId 用户购买记录主键
     * @return 结果
     */
    public int deleteAiUserPurchaseByPurchaseId(Long purchaseId);

    /**
     * 查询当前用户买的商品
     *
     * @return 积分商品集合
     */
    public List<AiUserPurchase> selectAiPointsProductListByUserId();

    /**
     * 判断用户是否购买了该商品
     */
    public boolean isPurchased(Long productId , Long userId);

    /**
     * 获取商品列表
     */
    boolean insertAiUserPurchase(Long productId , Long userId);
}
