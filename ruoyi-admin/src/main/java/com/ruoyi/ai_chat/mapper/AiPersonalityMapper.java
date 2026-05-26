package com.ruoyi.ai_chat.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ai_chat.domain.AiPersonality;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI性格Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Mapper
public interface AiPersonalityMapper extends BaseMapper<AiPersonality>
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
     * 删除AI性格
     * 
     * @param personalityId AI性格主键
     * @return 结果
     */
    public int deleteAiPersonalityByPersonalityId(Long personalityId);

    /**
     * 批量删除AI性格
     * 
     * @param personalityIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiPersonalityByPersonalityIds(Long[] personalityIds);


}
