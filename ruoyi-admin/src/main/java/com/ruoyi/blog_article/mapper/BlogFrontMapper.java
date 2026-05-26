package com.ruoyi.blog_article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.blog_article.domain.BlogFrontVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.Map;

@Mapper
public interface BlogFrontMapper  extends BaseMapper<BlogFrontVO> {
    // ==================== 自定义查询方法（带作者信息） ====================

    /**
     * 分页查询公开文章列表（带作者信息）
     *
     * @param page     分页对象
     * @param status   文章状态（0发布 1草稿），null则查询全部
     * @param category 文章分类（frontend/backend/life/ai），null则查询全部
     * @return 分页结果（包含作者昵称和头像）
     */
    @Select("<script>" +
            "SELECT a.*, u.nick_name as author_name, u.avatar as author_avatar " +
            "FROM blog_article a " +
            "LEFT JOIN sys_user u ON a.user_id = u.user_id " +
            "WHERE a.del_flag = '0' " +
            "<if test='status != null'> AND a.status = 0 </if>" +
            "<if test='category != null'> AND a.category = #{category} </if>" +
            "ORDER BY a.create_time DESC" +
            "</script>")
    IPage<BlogFrontVO> selectArticlePage(IPage<BlogFrontVO> page,
                                           @Param("status") String status,
                                           @Param("category") String category);

    /**
     * 查询用户文章列表（带作者信息）
     *
     * @param page   分页对象
     * @param userId 用户ID
     * @param status 文章状态（0发布 1草稿），null则查询全部
     * @return 分页结果（包含作者昵称和头像）
     */
    @Select("<script>" +
            "SELECT a.*, u.nick_name as author_name, u.avatar as author_avatar " +
            "FROM blog_article a " +
            "LEFT JOIN sys_user u ON a.user_id = u.user_id " +
            "WHERE a.del_flag = '0' AND a.user_id = #{userId} " +
            "<if test='status != null'> AND a.status = #{status} </if>" +
            "ORDER BY a.create_time DESC" +
            "</script>")
    IPage<BlogFrontVO> selectUserArticles(IPage<BlogFrontVO> page,
                                            @Param("userId") Long userId,
                                            @Param("status") String status);

    /**
     * 查询单篇文章详情（包含作者信息）
     *
     * @param id 文章ID
     * @return 文章详情（包含作者昵称和头像）
     */
    @Select("SELECT a.*, u.nick_name as author_name, u.avatar as author_avatar " +
            "FROM blog_article a " +
            "LEFT JOIN sys_user u ON a.user_id = u.user_id " +
            "WHERE a.id = #{id} AND a.del_flag = '0'")
    BlogFrontVO selectArticleWithAuthor(@Param("id") Long id);

    /**
     * 获取用户的文章统计数据
     *
     * @param userId 用户ID
     * @return Map 包含以下字段：
     *         - total_count: 文章总数
     *         - total_view: 总浏览量
     *         - total_like: 总点赞数
     *         - draft_count: 草稿数量
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(view_count) as total_view, " +
            "SUM(like_count) as total_like, " +
            "SUM(CASE WHEN status = '1' THEN 1 ELSE 0 END) as draft_count " +
            "FROM blog_article " +
            "WHERE user_id = #{userId}")
    Map<String, Object> selectStatistics(@Param("userId") Long userId);

}
