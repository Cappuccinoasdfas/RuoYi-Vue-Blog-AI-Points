package com.ruoyi.apiDoc.mapper;

import java.util.List;
import com.ruoyi.apiDoc.domain.SysApiDoc;

/**
 * API接口文档Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-19
 */
public interface SysApiDocMapper 
{
    /**
     * 查询API接口文档
     * 
     * @param id API接口文档主键
     * @return API接口文档
     */
    public SysApiDoc selectSysApiDocById(Long id);

    /**
     * 查询API接口文档列表
     * 
     * @param sysApiDoc API接口文档
     * @return API接口文档集合
     */
    public List<SysApiDoc> selectSysApiDocList(SysApiDoc sysApiDoc);

    /**
     * 新增API接口文档
     * 
     * @param sysApiDoc API接口文档
     * @return 结果
     */
    public int insertSysApiDoc(SysApiDoc sysApiDoc);

    /**
     * 修改API接口文档
     * 
     * @param sysApiDoc API接口文档
     * @return 结果
     */
    public int updateSysApiDoc(SysApiDoc sysApiDoc);

    /**
     * 删除API接口文档
     * 
     * @param id API接口文档主键
     * @return 结果
     */
    public int deleteSysApiDocById(Long id);

    /**
     * 批量删除API接口文档
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysApiDocByIds(Long[] ids);
}
