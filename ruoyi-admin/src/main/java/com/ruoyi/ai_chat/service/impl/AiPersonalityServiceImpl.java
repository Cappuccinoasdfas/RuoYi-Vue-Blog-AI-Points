package com.ruoyi.ai_chat.service.impl;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ai_chat.domain.AiChatMessage;
import com.ruoyi.ai_chat.domain.AiConfigProperties;
import com.ruoyi.ai_chat.service.*;
import com.ruoyi.ai_chat.service.python.PythonChatClient;
import com.ruoyi.common.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.ai_chat.mapper.AiPersonalityMapper;
import com.ruoyi.ai_chat.domain.AiPersonality;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
/**
 * AI性格Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiPersonalityServiceImpl extends ServiceImpl<AiPersonalityMapper, AiPersonality> implements IAiPersonalityService
{
    private final AiPersonalityMapper aiPersonalityMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AiConfigProperties aiConfig;
    /**
     * 加载性格配置到Redis
     * 优先加载用户自定义性格，没有则使用系统默认性格
     *
     * @param userId 用户ID
     */
    @Override
    public void loadPersonalityToRedis(Long userId) {
        String key = "personality:user:" + userId;

        // Redis已有缓存，直接返回
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return;
        }

        // 查询用户激活的性格配置
        LambdaQueryWrapper<AiPersonality> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiPersonality::getUserId, userId)
                .eq(AiPersonality::getIsActive, "1")
                .eq(AiPersonality::getStatus, "0");
        AiPersonality personality = aiPersonalityMapper.selectOne(wrapper);

        // 没有则使用默认性格（userId=0表示系统默认）
        if (personality == null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiPersonality::getUserId, 0L)
                    .eq(AiPersonality::getStatus, "0");
            personality = aiPersonalityMapper.selectOne(wrapper);
        }

        // 写入Redis，缓存时间从配置读取（默认24小时）
        if (personality != null) {
            int ttlHours = aiConfig.getCache().getPersonalityTtlHours();
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(personality),
                    ttlHours, TimeUnit.HOURS);
        }
    }
    /**
     * 查询AI性格
     *
     * @param personalityId AI性格主键
     * @return AI性格
     */
    @Override
    public AiPersonality selectAiPersonalityByPersonalityId(Long personalityId)
    {
        return aiPersonalityMapper.selectAiPersonalityByPersonalityId(personalityId);
    }

    /**
     * 查询AI性格列表
     *
     * @param aiPersonality AI性格
     * @return AI性格
     */
    @Override
    public List<AiPersonality> selectAiPersonalityList(AiPersonality aiPersonality)
    {
        return aiPersonalityMapper.selectAiPersonalityList(aiPersonality);
    }

    /**
     * 新增AI性格
     *
     * @param aiPersonality AI性格
     * @return 结果
     */
    @Override
    public int insertAiPersonality(AiPersonality aiPersonality)
    {
        aiPersonality.setCreateTime(DateUtils.getNowDate());
        return aiPersonalityMapper.insertAiPersonality(aiPersonality);
    }

    /**
     * 修改AI性格
     *
     * @param aiPersonality AI性格
     * @return 结果
     */
    @Override
    public int updateAiPersonality(AiPersonality aiPersonality)
    {
        aiPersonality.setUpdateTime(DateUtils.getNowDate());
        return aiPersonalityMapper.updateAiPersonality(aiPersonality);
    }

    /**
     * 批量删除AI性格
     *
     * @param personalityIds 需要删除的AI性格主键
     * @return 结果
     */
    @Override
    public int deleteAiPersonalityByPersonalityIds(Long[] personalityIds)
    {
        return aiPersonalityMapper.deleteAiPersonalityByPersonalityIds(personalityIds);
    }

    /**
     * 删除AI性格信息
     *
     * @param personalityId AI性格主键
     * @return 结果
     */
    @Override
    public int deleteAiPersonalityByPersonalityId(Long personalityId)
    {
        return aiPersonalityMapper.deleteAiPersonalityByPersonalityId(personalityId);
    }



}
