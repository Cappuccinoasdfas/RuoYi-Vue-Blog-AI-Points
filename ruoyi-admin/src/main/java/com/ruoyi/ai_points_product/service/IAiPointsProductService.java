package com.ruoyi.ai_points_product.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ai_chat.service.IAiChatService;
import com.ruoyi.ai_points_product.domain.AiPointsProduct;

/**
 * 积分商品Service接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
public interface IAiPointsProductService extends IService<AiPointsProduct>
{
    /**
     * 查询积分商品
     * 
     * @param productId 积分商品主键
     * @return 积分商品
     */
    public AiPointsProduct selectAiPointsProductByProductId(Long productId);

    /**
     * 查询积分商品列表
     * 
     * @param aiPointsProduct 积分商品
     * @return 积分商品集合
     */
    public List<AiPointsProduct> selectAiPointsProductList(AiPointsProduct aiPointsProduct);

    /**
     * 新增积分商品
     * 
     * @param aiPointsProduct 积分商品
     * @return 结果
     */
    public int insertAiPointsProduct(AiPointsProduct aiPointsProduct);

    /**
     * 修改积分商品
     * 
     * @param aiPointsProduct 积分商品
     * @return 结果
     */
    public int updateAiPointsProduct(AiPointsProduct aiPointsProduct);

    /**
     * 批量删除积分商品
     * 
     * @param productIds 需要删除的积分商品主键集合
     * @return 结果
     */
    public int deleteAiPointsProductByProductIds(Long[] productIds);

    /**
     * 删除积分商品信息
     * 
     * @param productId 积分商品主键
     * @return 结果
     */
    public int deleteAiPointsProductByProductId(Long productId);


    /**
     * 购买
     */
    public Long getProductPrice(Long productId);

}
