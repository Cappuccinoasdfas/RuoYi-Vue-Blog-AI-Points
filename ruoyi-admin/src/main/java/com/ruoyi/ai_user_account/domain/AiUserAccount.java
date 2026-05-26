package com.ruoyi.ai_user_account.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户积分账户对象 ai_user_account
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Data
@TableName("ai_user_account")
public class AiUserAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    /** 账户ID，主键自增 */
    private Long accountId;
    /** 用户ID，关联sys_user.user_id */
    @Excel(name = "用户ID，关联sys_user.user_id")
    private Long userId;
    /** 当前积分余额，默认0 */
    @Excel(name = "当前积分余额，默认10")
    private Long balance;
    /** 累计获得积分总额 */
    @Excel(name = "累计获得积分总额")
    private Long totalEarned;
    /** 累计消费积分总额 */
    @Excel(name = "累计消费积分总额")
    private Long totalSpent;
    /** 乐观锁版本号，每次更新+1 */
    @Excel(name = "乐观锁版本号，每次更新+1")
    @Version
    private Long version;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("accountId", getAccountId())
            .append("userId", getUserId())
            .append("balance", getBalance())
            .append("totalEarned", getTotalEarned())
            .append("totalSpent", getTotalSpent())
            .append("version", getVersion())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
