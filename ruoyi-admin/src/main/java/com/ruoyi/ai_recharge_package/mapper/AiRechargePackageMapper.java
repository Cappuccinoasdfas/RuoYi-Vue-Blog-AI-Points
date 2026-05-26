package com.ruoyi.ai_recharge_package.mapper;

import java.util.List;
import com.ruoyi.ai_recharge_package.domain.AiRechargePackage;

/**
 * 充值套餐Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-28
 */
public interface AiRechargePackageMapper 
{
    /**
     * 查询充值套餐
     * 
     * @param packageId 充值套餐主键
     * @return 充值套餐
     */
    public AiRechargePackage selectAiRechargePackageByPackageId(Long packageId);

    /**
     * 查询充值套餐列表
     * 
     * @param aiRechargePackage 充值套餐
     * @return 充值套餐集合
     */
    public List<AiRechargePackage> selectAiRechargePackageList(AiRechargePackage aiRechargePackage);

    /**
     * 新增充值套餐
     * 
     * @param aiRechargePackage 充值套餐
     * @return 结果
     */
    public int insertAiRechargePackage(AiRechargePackage aiRechargePackage);

    /**
     * 修改充值套餐
     * 
     * @param aiRechargePackage 充值套餐
     * @return 结果
     */
    public int updateAiRechargePackage(AiRechargePackage aiRechargePackage);

    /**
     * 删除充值套餐
     * 
     * @param packageId 充值套餐主键
     * @return 结果
     */
    public int deleteAiRechargePackageByPackageId(Long packageId);

    /**
     * 批量删除充值套餐
     * 
     * @param packageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiRechargePackageByPackageIds(Long[] packageIds);
}
