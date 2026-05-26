package com.ruoyi.ai_user_account.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ai_user_account.domain.AiUserAccount;

/**
 * 用户积分账户Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
public interface AiUserAccountMapper extends BaseMapper<AiUserAccount>
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
     * 删除用户积分账户
     * 
     * @param accountId 用户积分账户主键
     * @return 结果
     */
    public int deleteAiUserAccountByAccountId(Long accountId);

    /**
     * 批量删除用户积分账户
     * 
     * @param accountIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiUserAccountByAccountIds(Long[] accountIds);

}
