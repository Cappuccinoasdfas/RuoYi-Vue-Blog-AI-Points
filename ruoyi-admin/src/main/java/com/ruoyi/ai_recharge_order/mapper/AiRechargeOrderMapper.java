package com.ruoyi.ai_recharge_order.mapper;

import java.util.List;
import com.ruoyi.ai_recharge_order.domain.AiRechargeOrder;

/**
 * 充值订单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
public interface AiRechargeOrderMapper 
{
    /**
     * 查询充值订单
     * 
     * @param orderId 充值订单主键
     * @return 充值订单
     */
    public AiRechargeOrder selectAiRechargeOrderByOrderId(Long orderId);

    /**
     * 查询充值订单列表
     * 
     * @param aiRechargeOrder 充值订单
     * @return 充值订单集合
     */
    public List<AiRechargeOrder> selectAiRechargeOrderList(AiRechargeOrder aiRechargeOrder);

    /**
     * 新增充值订单
     * 
     * @param aiRechargeOrder 充值订单
     * @return 结果
     */
    public int insertAiRechargeOrder(AiRechargeOrder aiRechargeOrder);

    /**
     * 修改充值订单
     * 
     * @param aiRechargeOrder 充值订单
     * @return 结果
     */
    public int updateAiRechargeOrder(AiRechargeOrder aiRechargeOrder);

    /**
     * 删除充值订单
     * 
     * @param orderId 充值订单主键
     * @return 结果
     */
    public int deleteAiRechargeOrderByOrderId(Long orderId);

    /**
     * 批量删除充值订单
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiRechargeOrderByOrderIds(Long[] orderIds);
}
