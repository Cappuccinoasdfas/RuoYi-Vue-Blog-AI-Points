package com.ruoyi.ai_recharge_package.service;

import java.util.List;
import com.ruoyi.ai_recharge_package.domain.AiRechargePackage;

/**
 * 充值套餐Service接口
 * 
 * @author ruoyi
 * @date 2026-04-28
 */
public interface IAiRechargePackageService 
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
     * 批量删除充值套餐
     * 
     * @param packageIds 需要删除的充值套餐主键集合
     * @return 结果
     */
    public int deleteAiRechargePackageByPackageIds(Long[] packageIds);

    /**
     * 删除充值套餐信息
     * 
     * @param packageId 充值套餐主键
     * @return 结果
     */
    public int deleteAiRechargePackageByPackageId(Long packageId);
}
