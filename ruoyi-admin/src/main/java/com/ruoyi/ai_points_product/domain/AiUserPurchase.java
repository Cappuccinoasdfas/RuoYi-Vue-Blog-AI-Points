package com.ruoyi.ai_points_product.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户购买记录对象 ai_user_purchase
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Data
@TableName("ai_user_purchase")
public class AiUserPurchase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 购买记录ID，主键自增 */
    private Long purchaseId;

    /** 用户ID，关联sys_user.user_id */
    @Excel(name = "用户ID，关联sys_user.user_id")
    private Long userId;

    /** 商品ID，关联ai_points_product.product_id */
    @Excel(name = "商品ID，关联ai_points_product.product_id")
    private Long productId;

    /** 购买时消耗的积分数 */
    @Excel(name = "购买时消耗的积分数")
    private Long pointsCost;

    /** 过期时间，NULL表示永久有效 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过期时间，NULL表示永久有效", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireTime;

    /** 状态：0已失效 1有效 */
    @Excel(name = "状态：0已失效 1有效")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("purchaseId", getPurchaseId())
            .append("userId", getUserId())
            .append("productId", getProductId())
            .append("pointsCost", getPointsCost())
            .append("expireTime", getExpireTime())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
