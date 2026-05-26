package com.ruoyi.ai_recharge_package.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_recharge_package.mapper.AiRechargePackageMapper;
import com.ruoyi.ai_recharge_package.domain.AiRechargePackage;
import com.ruoyi.ai_recharge_package.service.IAiRechargePackageService;

/**
 * 充值套餐Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-28
 */
@Service
public class AiRechargePackageServiceImpl implements IAiRechargePackageService 
{
    @Autowired
    private AiRechargePackageMapper aiRechargePackageMapper;

    /**
     * 查询充值套餐
     * 
     * @param packageId 充值套餐主键
     * @return 充值套餐
     */
    @Override
    public AiRechargePackage selectAiRechargePackageByPackageId(Long packageId)
    {
        return aiRechargePackageMapper.selectAiRechargePackageByPackageId(packageId);
    }

    /**
     * 查询充值套餐列表
     * 
     * @param aiRechargePackage 充值套餐
     * @return 充值套餐
     */
    @Override
    public List<AiRechargePackage> selectAiRechargePackageList(AiRechargePackage aiRechargePackage)
    {
        return aiRechargePackageMapper.selectAiRechargePackageList(aiRechargePackage);
    }

    /**
     * 新增充值套餐
     * 
     * @param aiRechargePackage 充值套餐
     * @return 结果
     */
    @Override
    public int insertAiRechargePackage(AiRechargePackage aiRechargePackage)
    {
        aiRechargePackage.setCreateTime(DateUtils.getNowDate());
        return aiRechargePackageMapper.insertAiRechargePackage(aiRechargePackage);
    }

    /**
     * 修改充值套餐
     * 
     * @param aiRechargePackage 充值套餐
     * @return 结果
     */
    @Override
    public int updateAiRechargePackage(AiRechargePackage aiRechargePackage)
    {
        aiRechargePackage.setUpdateTime(DateUtils.getNowDate());
        return aiRechargePackageMapper.updateAiRechargePackage(aiRechargePackage);
    }

    /**
     * 批量删除充值套餐
     * 
     * @param packageIds 需要删除的充值套餐主键
     * @return 结果
     */
    @Override
    public int deleteAiRechargePackageByPackageIds(Long[] packageIds)
    {
        return aiRechargePackageMapper.deleteAiRechargePackageByPackageIds(packageIds);
    }

    /**
     * 删除充值套餐信息
     * 
     * @param packageId 充值套餐主键
     * @return 结果
     */
    @Override
    public int deleteAiRechargePackageByPackageId(Long packageId)
    {
        return aiRechargePackageMapper.deleteAiRechargePackageByPackageId(packageId);
    }
}
