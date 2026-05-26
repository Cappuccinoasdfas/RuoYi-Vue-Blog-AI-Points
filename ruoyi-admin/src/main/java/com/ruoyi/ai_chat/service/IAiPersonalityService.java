package com.ruoyi.ai_chat.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ai_chat.domain.AiPersonality;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI性格Service接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
public interface IAiPersonalityService extends IService<AiPersonality>
{
    /**
     * 查询AI性格
     * 
     * @param personalityId AI性格主键
     * @return AI性格
     */
    public AiPersonality selectAiPersonalityByPersonalityId(Long personalityId);

    /**
     * 查询AI性格列表
     * 
     * @param aiPersonality AI性格
     * @return AI性格集合
     */
    public List<AiPersonality> selectAiPersonalityList(AiPersonality aiPersonality);

    /**
     * 新增AI性格
     * 
     * @param aiPersonality AI性格
     * @return 结果
     */
    public int insertAiPersonality(AiPersonality aiPersonality);

    /**
     * 修改AI性格
     * 
     * @param aiPersonality AI性格
     * @return 结果
     */
    public int updateAiPersonality(AiPersonality aiPersonality);

    /**
     * 批量删除AI性格
     * 
     * @param personalityIds 需要删除的AI性格主键集合
     * @return 结果
     */
    public int deleteAiPersonalityByPersonalityIds(Long[] personalityIds);

    /**
     * 删除AI性格信息
     *
     * @param personalityId AI性格主键
     * @return 结果
     */
    public int deleteAiPersonalityByPersonalityId(Long personalityId);

    /**
     * 写入redis
     * @param userId 用户id
     * 查询当前登录用户的性格文件
     * @return
     */
    void loadPersonalityToRedis(Long userId);

}
