package com.ruoyi.ai_points_product.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ai_points_product.service.IAiUserPurchaseService;
import com.ruoyi.ai_user_account.service.IAiUserAccountService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_points_product.mapper.AiPointsProductMapper;
import com.ruoyi.ai_points_product.domain.AiPointsProduct;
import com.ruoyi.ai_points_product.service.IAiPointsProductService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分商品Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Service
@RequiredArgsConstructor
public class AiPointsProductServiceImpl extends ServiceImpl<AiPointsProductMapper, AiPointsProduct> implements IAiPointsProductService
{
    private final AiPointsProductMapper aiPointsProductMapper;
    /**
     * 查询积分商品
     * 
     * @param productId 积分商品主键
     * @return 积分商品
     */
    @Override
    public AiPointsProduct selectAiPointsProductByProductId(Long productId)
    {
        return aiPointsProductMapper.selectAiPointsProductByProductId(productId);
    }

    /**
     * 查询积分商品列表
     * 
     * @param aiPointsProduct 积分商品
     * @return 积分商品
     */
    @Override
    public List<AiPointsProduct> selectAiPointsProductList(AiPointsProduct aiPointsProduct)
    {
        return aiPointsProductMapper.selectAiPointsProductList(aiPointsProduct);
    }

    /**
     * 新增积分商品
     * 
     * @param aiPointsProduct 积分商品
     * @return 结果
     */
    @Override
    public int insertAiPointsProduct(AiPointsProduct aiPointsProduct)
    {
        aiPointsProduct.setCreateTime(DateUtils.getNowDate());
        return aiPointsProductMapper.insertAiPointsProduct(aiPointsProduct);
    }

    /**
     * 修改积分商品
     * 
     * @param aiPointsProduct 积分商品
     * @return 结果
     */
    @Override
    public int updateAiPointsProduct(AiPointsProduct aiPointsProduct)
    {
        return aiPointsProductMapper.updateAiPointsProduct(aiPointsProduct);
    }

    /**
     * 批量删除积分商品
     * 
     * @param productIds 需要删除的积分商品主键
     * @return 结果
     */
    @Override
    public int deleteAiPointsProductByProductIds(Long[] productIds)
    {
        return aiPointsProductMapper.deleteAiPointsProductByProductIds(productIds);
    }

    /**
     * 删除积分商品信息
     * 
     * @param productId 积分商品主键
     * @return 结果
     */
    @Override
    public int deleteAiPointsProductByProductId(Long productId)
    {
        return aiPointsProductMapper.deleteAiPointsProductByProductId(productId);
    }

    /**
     * 购买商品
     * @param productId
     * @return
     */
    @Override
    public Long getProductPrice(Long productId) {
        AiPointsProduct product = aiPointsProductMapper.selectAiPointsProductByProductId(productId);;
        return product != null ? product.getPrice() : 0L;
    }
}
