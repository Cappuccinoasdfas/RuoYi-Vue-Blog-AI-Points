package com.ruoyi.ai_points_product.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ai_points_product.domain.AiUserPurchase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户购买记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Mapper
public interface AiUserPurchaseMapper extends BaseMapper<AiUserPurchase>
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
     * 删除用户购买记录
     * 
     * @param purchaseId 用户购买记录主键
     * @return 结果
     */
    public int deleteAiUserPurchaseByPurchaseId(Long purchaseId);

    /**
     * 批量删除用户购买记录
     * 
     * @param purchaseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiUserPurchaseByPurchaseIds(Long[] purchaseIds);
}
