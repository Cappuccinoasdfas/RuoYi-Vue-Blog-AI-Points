package com.ruoyi.ai_recharge_order.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_recharge_order.mapper.AiRechargeOrderMapper;
import com.ruoyi.ai_recharge_order.domain.AiRechargeOrder;
import com.ruoyi.ai_recharge_order.service.IAiRechargeOrderService;

/**
 * 充值订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Service
public class AiRechargeOrderServiceImpl implements IAiRechargeOrderService 
{
    @Autowired
    private AiRechargeOrderMapper aiRechargeOrderMapper;

    /**
     * 查询充值订单
     * 
     * @param orderId 充值订单主键
     * @return 充值订单
     */
    @Override
    public AiRechargeOrder selectAiRechargeOrderByOrderId(Long orderId)
    {
        return aiRechargeOrderMapper.selectAiRechargeOrderByOrderId(orderId);
    }

    /**
     * 查询充值订单列表
     * 
     * @param aiRechargeOrder 充值订单
     * @return 充值订单
     */
    @Override
    public List<AiRechargeOrder> selectAiRechargeOrderList(AiRechargeOrder aiRechargeOrder)
    {
        return aiRechargeOrderMapper.selectAiRechargeOrderList(aiRechargeOrder);
    }

    /**
     * 新增充值订单
     * 
     * @param aiRechargeOrder 充值订单
     * @return 结果
     */
    @Override
    public int insertAiRechargeOrder(AiRechargeOrder aiRechargeOrder)
    {
        aiRechargeOrder.setCreateTime(DateUtils.getNowDate());
        return aiRechargeOrderMapper.insertAiRechargeOrder(aiRechargeOrder);
    }

    /**
     * 修改充值订单
     * 
     * @param aiRechargeOrder 充值订单
     * @return 结果
     */
    @Override
    public int updateAiRechargeOrder(AiRechargeOrder aiRechargeOrder)
    {
        return aiRechargeOrderMapper.updateAiRechargeOrder(aiRechargeOrder);
    }

    /**
     * 批量删除充值订单
     * 
     * @param orderIds 需要删除的充值订单主键
     * @return 结果
     */
    @Override
    public int deleteAiRechargeOrderByOrderIds(Long[] orderIds)
    {
        return aiRechargeOrderMapper.deleteAiRechargeOrderByOrderIds(orderIds);
    }

    /**
     * 删除充值订单信息
     * 
     * @param orderId 充值订单主键
     * @return 结果
     */
    @Override
    public int deleteAiRechargeOrderByOrderId(Long orderId)
    {
        return aiRechargeOrderMapper.deleteAiRechargeOrderByOrderId(orderId);
    }

    @Override
    public String createOrder(Long userId, Long packageId) {
        return null;
    }
}
