package com.ruoyi.apiDoc.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.apiDoc.mapper.SysApiDocMapper;
import com.ruoyi.apiDoc.domain.SysApiDoc;
import com.ruoyi.apiDoc.service.ISysApiDocService;

/**
 * API接口文档Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-19
 */
@Service
public class SysApiDocServiceImpl implements ISysApiDocService 
{
    @Autowired
    private SysApiDocMapper sysApiDocMapper;

    /**
     * 查询API接口文档
     * 
     * @param id API接口文档主键
     * @return API接口文档
     */
    @Override
    public SysApiDoc selectSysApiDocById(Long id)
    {
        return sysApiDocMapper.selectSysApiDocById(id);
    }

    /**
     * 查询API接口文档列表
     * 
     * @param sysApiDoc API接口文档
     * @return API接口文档
     */
    @Override
    public List<SysApiDoc> selectSysApiDocList(SysApiDoc sysApiDoc)
    {
        return sysApiDocMapper.selectSysApiDocList(sysApiDoc);
    }

    /**
     * 新增API接口文档
     * 
     * @param sysApiDoc API接口文档
     * @return 结果
     */
    @Override
    public int insertSysApiDoc(SysApiDoc sysApiDoc)
    {
        sysApiDoc.setCreateTime(DateUtils.getNowDate());
        return sysApiDocMapper.insertSysApiDoc(sysApiDoc);
    }

    /**
     * 修改API接口文档
     * 
     * @param sysApiDoc API接口文档
     * @return 结果
     */
    @Override
    public int updateSysApiDoc(SysApiDoc sysApiDoc)
    {
        sysApiDoc.setUpdateTime(DateUtils.getNowDate());
        return sysApiDocMapper.updateSysApiDoc(sysApiDoc);
    }

    /**
     * 批量删除API接口文档
     * 
     * @param ids 需要删除的API接口文档主键
     * @return 结果
     */
    @Override
    public int deleteSysApiDocByIds(Long[] ids)
    {
        return sysApiDocMapper.deleteSysApiDocByIds(ids);
    }

    /**
     * 删除API接口文档信息
     * 
     * @param id API接口文档主键
     * @return 结果
     */
    @Override
    public int deleteSysApiDocById(Long id)
    {
        return sysApiDocMapper.deleteSysApiDocById(id);
    }
}
