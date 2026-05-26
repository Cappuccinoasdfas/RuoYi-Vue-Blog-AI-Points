package com.ruoyi.blog_comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.blog_comment.domain.BlogFrontComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BlogCommentFrontMapper extends BaseMapper<BlogFrontComment> {

    /**
     * 获取顶级评论分页（带子评论数量）
     */
    @Select("SELECT c.*, u.user_name as user_name, u.avatar as user_avatar, " +
            "COALESCE(t.reply_count, 0) as reply_count " +
            "FROM blog_comment c " +
            "LEFT JOIN sys_user u ON c.user_id = u.user_id " +
            "LEFT JOIN ( " +
            "   SELECT parent_id, COUNT(*) as reply_count " +
            "   FROM blog_comment " +
            "   WHERE status = '0' AND parent_id != '0' " +
            "   GROUP BY parent_id " +
            ") t ON c.id = t.parent_id " +
            "WHERE c.article_id = #{articleId} AND c.parent_id = '0' AND c.status = '0' " +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<BlogFrontComment> selectTopLevelComments(@Param("articleId") Long articleId,
                                                  @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit);


    /**
     * 获取所有子评论（用于查看更多回复）
     */
    // 改名，避免与 BaseMapper 方法冲突
    @Select("SELECT c.*, u.user_name as user_name, u.avatar as user_avatar, " +
            "ru.user_name as reply_to_user_name " +
            "FROM blog_comment c " +
            "LEFT JOIN sys_user u ON c.user_id = u.user_id " +
            "LEFT JOIN sys_user ru ON c.reply_to_user_id = ru.user_id " +
            "WHERE c.parent_id = #{parentId} AND c.status = '0' " +
            "ORDER BY c.create_time ASC " +
            "LIMIT #{offset}, #{pageSize}")
    List<BlogFrontComment> selectChildCommentsByParentId(@Param("parentId") String parentId,
                                                         @Param("offset") int offset,
                                                         @Param("pageSize") int pageSize);


}
