package com.ruoyi.ai_user_account.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ai_user_account.domain.AiUserAccount;

/**
 * 用户积分账户Service接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
public interface IAiUserAccountService extends IService<AiUserAccount>
{
    /**
     * 查询用户积分账户
     * 
     * @param accountId 用户积分账户主键
     * @return 用户积分账户
     */
    public AiUserAccount selectAiUserAccountByAccountId(Long accountId);

    /**
     * 查询用户积分账户列表
     * 
     * @param aiUserAccount 用户积分账户
     * @return 用户积分账户集合
     */
    public List<AiUserAccount> selectAiUserAccountList(AiUserAccount aiUserAccount);

    /**
     * 新增用户积分账户
     * 
     * @param aiUserAccount 用户积分账户
     * @return 结果
     */
    public int insertAiUserAccount(AiUserAccount aiUserAccount);

    /**
     * 修改用户积分账户
     * 
     * @param aiUserAccount 用户积分账户
     * @return 结果
     */
    public int updateAiUserAccount(AiUserAccount aiUserAccount);

    /**
     * 批量删除用户积分账户
     * 
     * @param accountIds 需要删除的用户积分账户主键集合
     * @return 结果
     */
    public int deleteAiUserAccountByAccountIds(Long[] accountIds);

    /**
     * 删除用户积分账户信息
     * 
     * @param accountId 用户积分账户主键
     * @return 结果
     */
    public int deleteAiUserAccountByAccountId(Long accountId);


    /*
     * 根据用户ID查询用户积分账户
     */
    public Long selectAiUserAccountBalanceByUserId(Long userId);

    /*
     * 根据商品id扣取用户积分
     */
    public boolean deductUserPoints(Long price , Long userId);
}
