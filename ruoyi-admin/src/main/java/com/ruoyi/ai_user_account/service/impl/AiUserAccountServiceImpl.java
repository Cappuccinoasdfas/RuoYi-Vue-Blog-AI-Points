package com.ruoyi.ai_user_account.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_user_account.mapper.AiUserAccountMapper;
import com.ruoyi.ai_user_account.domain.AiUserAccount;
import com.ruoyi.ai_user_account.service.IAiUserAccountService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户积分账户Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Service
@RequiredArgsConstructor
public class AiUserAccountServiceImpl extends ServiceImpl<AiUserAccountMapper, AiUserAccount> implements IAiUserAccountService
{
    private final AiUserAccountMapper aiUserAccountMapper;
    private static final Long DEFAULT_BALANCE = 10L;

    /**
     * 查询用户积分账户
     * 
     * @param accountId 用户积分账户主键
     * @return 用户积分账户
     */
    @Override
    public AiUserAccount selectAiUserAccountByAccountId(Long accountId)
    {
        return aiUserAccountMapper.selectAiUserAccountByAccountId(accountId);
    }

    /**
     * 查询用户积分账户列表
     * 
     * @param aiUserAccount 用户积分账户
     * @return 用户积分账户
     */
    @Override
    public List<AiUserAccount> selectAiUserAccountList(AiUserAccount aiUserAccount)
    {
        return aiUserAccountMapper.selectAiUserAccountList(aiUserAccount);
    }

    /**
     * 新增用户积分账户
     * 
     * @param aiUserAccount 用户积分账户
     * @return 结果
     */
    @Override
    public int insertAiUserAccount(AiUserAccount aiUserAccount)
    {
        aiUserAccount.setCreateTime(DateUtils.getNowDate());
        return aiUserAccountMapper.insertAiUserAccount(aiUserAccount);
    }

    /**
     * 修改用户积分账户
     * 
     * @param aiUserAccount 用户积分账户
     * @return 结果
     */
    @Override
    public int updateAiUserAccount(AiUserAccount aiUserAccount)
    {
        aiUserAccount.setUpdateTime(DateUtils.getNowDate());
        return aiUserAccountMapper.updateAiUserAccount(aiUserAccount);
    }

    /**
     * 批量删除用户积分账户
     * 
     * @param accountIds 需要删除的用户积分账户主键
     * @return 结果
     */
    @Override
    public int deleteAiUserAccountByAccountIds(Long[] accountIds)
    {
        return aiUserAccountMapper.deleteAiUserAccountByAccountIds(accountIds);
    }

    /**
     * 删除用户积分账户信息
     * 
     * @param accountId 用户积分账户主键
     * @return 结果
     */
    @Override
    public int deleteAiUserAccountByAccountId(Long accountId)
    {
        return aiUserAccountMapper.deleteAiUserAccountByAccountId(accountId);
    }

    /**
     * 根据用户ID查询用户积分账户余额
     *
     * @param userId 用户ID
     * @return 积分余额
     */
    @Override
    public Long selectAiUserAccountBalanceByUserId(Long userId) {
        AiUserAccount account = aiUserAccountMapper.selectOne(
                new LambdaQueryWrapper<AiUserAccount>().eq(AiUserAccount::getUserId, userId)
        );
        if (account != null) {
            return account.getBalance();
        }

        // ✅ 这里创建新账户！
        account = new AiUserAccount();
        account.setUserId(userId);
        account.setBalance(DEFAULT_BALANCE);
        account.setTotalEarned(DEFAULT_BALANCE);
        account.setVersion(1L);
        aiUserAccountMapper.insert(account);

        return DEFAULT_BALANCE;
    }

    /**
     * 扣减用户积分
     *
     * @param price 积分价格
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    public boolean deductUserPoints(Long price, Long userId) {
        // 用乐观锁保证原子性
        LambdaUpdateWrapper<AiUserAccount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiUserAccount::getUserId, userId)
                .ge(AiUserAccount::getBalance, price)
                .setSql("balance = balance - " + price)
                .setSql("total_spent = total_spent + " + price);

        return aiUserAccountMapper.update(null, updateWrapper) > 0;
    }

}
