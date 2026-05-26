package com.ruoyi.ai_points_product.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ai_points_product.domain.AiPointsProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分商品Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */

@Mapper
public interface AiPointsProductMapper extends BaseMapper<AiPointsProduct>
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
     * 删除积分商品
     * 
     * @param productId 积分商品主键
     * @return 结果
     */
    public int deleteAiPointsProductByProductId(Long productId);

    /**
     * 批量删除积分商品
     * 
     * @param productIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiPointsProductByProductIds(Long[] productIds);
}
