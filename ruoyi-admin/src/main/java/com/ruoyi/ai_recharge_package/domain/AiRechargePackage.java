package com.ruoyi.ai_recharge_package.domain;

import java.math.BigDecimal;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 充值套餐对象 ai_recharge_package
 * 
 * @author ruoyi
 * @date 2026-04-28
 */
@Data
public class AiRechargePackage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 套餐ID */
    private Long packageId;

    /** 套餐名称 */
    @Excel(name = "套餐名称")
    private String packageName;

    /** 充值金额（元） */
    @Excel(name = "充值金额", readConverterExp = "元=")
    private BigDecimal price;

    /** 基础积分 */
    @Excel(name = "基础积分")
    private Long basePoints;

    /** 额外赠送积分 */
    @Excel(name = "额外赠送积分")
    private Long bonusPoints;

    /** 总获得积分（虚拟列） */
    @Excel(name = "总获得积分", readConverterExp = "虚=拟列")
    private Long totalPoints;

    /** 优惠标签 */
    @Excel(name = "优惠标签")
    private String discountLabel;

    /** 是否推荐 0否 1是 */
    @Excel(name = "是否推荐 0否 1是")
    private Integer isRecommend;

    /** 排序 */
    @Excel(name = "排序")
    private Long sortOrder;

    /** 状态 0上架 1下架 */
    @Excel(name = "状态 0上架 1下架")
    private Integer status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("packageId", getPackageId())
            .append("packageName", getPackageName())
            .append("price", getPrice())
            .append("basePoints", getBasePoints())
            .append("bonusPoints", getBonusPoints())
            .append("totalPoints", getTotalPoints())
            .append("discountLabel", getDiscountLabel())
            .append("isRecommend", getIsRecommend())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
