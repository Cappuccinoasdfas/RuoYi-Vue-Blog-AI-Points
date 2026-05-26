package com.ruoyi.blog_article.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmailMapper {
    // 统计该邮箱是否存在
    @Select("SELECT COUNT(*) FROM sys_user WHERE email = #{email} AND del_flag = '0'")
    int countByEmail(String email);
}
