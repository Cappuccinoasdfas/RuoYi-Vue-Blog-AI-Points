package com.ruoyi.apiDoc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * API接口文档对象 sys_api_doc
 * 
 * @author ruoyi
 * @date 2026-04-19
 */
public class SysApiDoc extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 模块名称 */
    @Excel(name = "模块名称")
    private String moduleName;

    /** 接口名称/简要描述 */
    @Excel(name = "接口名称/简要描述")
    private String apiName;

    /** 请求URL */
    @Excel(name = "请求URL")
    private String requestUrl;

    /** 请求方式(GET/POST/PUT/DELETE) */
    @Excel(name = "请求方式(GET/POST/PUT/DELETE)")
    private String requestMethod;

    /** 请求参数(JSON格式) */
    @Excel(name = "请求参数(JSON格式)")
    private String requestParams;

    /** 返回示例(JSON格式) */
    @Excel(name = "返回示例(JSON格式)")
    private String responseExample;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long sortOrder;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态(0正常 1停用)")
    private String status;

    /** 删除标志(0正常 2删除) */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setModuleName(String moduleName) 
    {
        this.moduleName = moduleName;
    }

    public String getModuleName() 
    {
        return moduleName;
    }

    public void setApiName(String apiName) 
    {
        this.apiName = apiName;
    }

    public String getApiName() 
    {
        return apiName;
    }

    public void setRequestUrl(String requestUrl) 
    {
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() 
    {
        return requestUrl;
    }

    public void setRequestMethod(String requestMethod) 
    {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() 
    {
        return requestMethod;
    }

    public void setRequestParams(String requestParams) 
    {
        this.requestParams = requestParams;
    }

    public String getRequestParams() 
    {
        return requestParams;
    }

    public void setResponseExample(String responseExample) 
    {
        this.responseExample = responseExample;
    }

    public String getResponseExample() 
    {
        return responseExample;
    }

    public void setSortOrder(Long sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder() 
    {
        return sortOrder;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("moduleName", getModuleName())
            .append("apiName", getApiName())
            .append("requestUrl", getRequestUrl())
            .append("requestMethod", getRequestMethod())
            .append("requestParams", getRequestParams())
            .append("responseExample", getResponseExample())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
