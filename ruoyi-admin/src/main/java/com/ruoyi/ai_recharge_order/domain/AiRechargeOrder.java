package com.ruoyi.ai_recharge_order.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 充值订单对象 ai_recharge_order
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Data
public class AiRechargeOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID，主键自增 */
    private Long orderId;

    /** 订单号，唯一，如：RECH20241220123456 */
    @Excel(name = "订单号，唯一，如：RECH20241220123456")
    private String orderNo;

    /** 用户ID，关联sys_user.user_id */
    @Excel(name = "用户ID，关联sys_user.user_id")
    private Long userId;

    /** 充值金额，单位：分（1元=100分） */
    @Excel(name = "充值金额，单位：分", readConverterExp = "1=元=100分")
    private Long amount;

    /** 充值获得的积分数 */
    @Excel(name = "充值获得的积分数")
    private Long points;

    /** 订单状态：pending待支付 success成功 failed失败 */
    @Excel(name = "订单状态：pending待支付 success成功 failed失败")
    private String status;

    /** 支付完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "支付完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("userId", getUserId())
            .append("amount", getAmount())
            .append("points", getPoints())
            .append("status", getStatus())
            .append("payTime", getPayTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
