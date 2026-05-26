package com.ruoyi.ai_points_product.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分商品对象 ai_points_product
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Data
@TableName("ai_points_product")
public class AiPointsProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID，主键自增 */
    private Long productId;

    /** 商品名称，如：自定义AI性格 */
    @Excel(name = "商品名称，如：自定义AI性格")
    private String productName;

    /** 商品类型，如：personality_custom/chat_feature */
    @Excel(name = "商品类型，如：personality_custom/chat_feature")
    private String productType;

    /** 购买所需积分数 */
    @Excel(name = "购买所需积分数")
    private Long price;

    /** 有效期天数，0表示永久有效 */
    @Excel(name = "有效期天数，0表示永久有效")
    private Long durationDays;

    /** 商品描述，展示给用户看 */
    @Excel(name = "商品描述，展示给用户看")
    private String description;

    /** 状态：0正常 1禁用 */
    @Excel(name = "状态：0正常 1禁用")
    private String status;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("productType", getProductType())
            .append("price", getPrice())
            .append("durationDays", getDurationDays())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
