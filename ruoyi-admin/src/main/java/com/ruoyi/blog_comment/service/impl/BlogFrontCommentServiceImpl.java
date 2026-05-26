package com.ruoyi.blog_comment.service.impl;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.blog_article.domain.BlogFrontVO;
import com.ruoyi.blog_article.mapper.BlogFrontMapper;
import com.ruoyi.blog_comment.domain.BlogFrontComment;
import com.ruoyi.blog_comment.mapper.BlogCommentFrontMapper;
import com.ruoyi.blog_comment.service.IBlogFrontCommentService;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.utils.BlogUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客评论服务实现类
 *
 * @author ruoyi
 * @date 2026-04-14
 */
@Service
@RequiredArgsConstructor
public class BlogFrontCommentServiceImpl extends ServiceImpl<BlogCommentFrontMapper, BlogFrontComment> implements IBlogFrontCommentService {
    // 评论Mapper
    private  final BlogCommentFrontMapper commentMapper;
    // URL处理工具
    private final BlogUrlUtils blogUrlUtils;
    //文章相关表
    private final BlogFrontMapper frontMapper;

    /**
     * 获取评论分页列表（包含子评论预览）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页数量
     * @param articleId 文章ID
     * @return 评论分页对象，包含顶级评论及其子评论预览（最多3条）
     */
    @Override
    public IPage<BlogFrontComment> getCommentPage(Integer pageNum, Integer pageSize, Long articleId) {
        // 1. 查询顶级评论（已包含子评论数量）
        int offset = (pageNum - 1) * pageSize;
        List<BlogFrontComment> topLevelComments = commentMapper.selectTopLevelComments(articleId, offset, pageSize);

        // 2. 统计总记录数
        Long total = lambdaQuery()
                .eq(BlogFrontComment::getArticleId, articleId)
                .eq(BlogFrontComment::getParentId, "0")
                .eq(BlogFrontComment::getStatus, "0")
                .count();
        // 3. 处理URL
        for (BlogFrontComment comment : topLevelComments) {
            blogUrlUtils.processCommentUrls(comment);
            comment.setChildren(null); // 不需要子评论列表
        }
        // 4. 构建分页对象
        Page<BlogFrontComment> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(topLevelComments);
        return page;
    }
    /**
     * 统计总评论
     * 是用来获取BlogFrontVo文章的数据的
     */
    @Override
    public Long getTotalCommentCount(Long articleId){
        BlogFrontVO vo = frontMapper.selectById(articleId);
        return vo != null ? vo.getCommentCount() : 0L;
    }
    /**
     * 发表评论
     *
     * @param comment 评论实体对象
     * @return 保存后的评论对象（包含生成的ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlogFrontComment addComment(BlogFrontComment comment) {
        // 1. 设置评论默认值
        comment.setUserId(SecurityUtils.getUserId());           // 当前登录用户ID
        comment.setLikeCount(0);                                // 初始点赞数为0
        comment.setStatus("0");                                 // 状态：0-正常
        comment.setCreateTime(LocalDateTime.now());             // 创建时间
        comment.setUpdateTime(LocalDateTime.now());             // 更新时间

        // 2. 如果没有父评论ID，设置为'0'表示顶级评论
        if (comment.getParentId() == null) {
            comment.setParentId("0");
        }

        // 3. 保存评论到数据库（MyBatis-Plus 会自动生成雪花ID）
        save(comment);

        // 4. 更新文章的评论总数
        updateArticleCommentCount(comment.getArticleId());

        return comment;
    }

    /**
     * 获取更多子评论（用于"查看更多回复"功能）
     *
     * @param parentId 父评论ID
     * @param pageNum  当前页码
     * @param pageSize 每页数量
     * @return 子评论列表
     */
    @Override
    public List<BlogFrontComment> getMoreChildComments(String parentId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<BlogFrontComment> blogFrontComments =
                commentMapper.selectChildCommentsByParentId(parentId, offset, pageSize);

        // 处理每个子评论的头像URL
        for (BlogFrontComment comment : blogFrontComments) {
            blogUrlUtils.processCommentUrls(comment);
        }

        return blogFrontComments;
    }


    /**
     * 删除评论（软删除）
     *
     * @param commentId 评论ID
     * @param userId    当前操作用户ID（用于权限校验）
     * @return true-删除成功，false-删除失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(String commentId, Long userId) {
        // 1. 根据ID查询评论
        BlogFrontComment comment = getById(commentId);  // 直接传String，mybatis-plus支持
        if (comment == null) {
            return false;
        }

        // 2. 权限校验：只有评论作者或管理员可以删除
        if (!comment.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            return false;
        }

        // 3. 软删除当前评论
        comment.setStatus("2");
        comment.setUpdateTime(LocalDateTime.now());
        boolean result = updateById(comment);

        // 4. 如果是父评论（parent_id = 0），递归软删除所有子评论
        if (comment.getParentId().equals("0")) {
            // 软删除所有子评论
            LambdaUpdateWrapper<BlogFrontComment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BlogFrontComment::getParentId, commentId)
                    .set(BlogFrontComment::getStatus, "2")
                    .set(BlogFrontComment::getUpdateTime, LocalDateTime.now());
            commentMapper.update(null, updateWrapper);
        }

        // 5. 删除成功后，更新文章的评论总数（只更新一次）
        if (result) {
            updateArticleCommentCount(comment.getArticleId());
        }

        return result;
    }

    /**
     * 更新文章的评论总数
     * <p>
     * 统计指定文章下的所有有效评论数量（包含顶级评论和子评论），
     * 并更新到 blog_article 表的 comment_count 字段
     * </p>
     * @param articleId 文章ID
     */
    public void updateArticleCommentCount(Long articleId) {
        if (articleId == null) {
            return;
        }

        // 统计该文章下的评论数量
        Long commentCount = lambdaQuery()
                .eq(BlogFrontComment::getArticleId, articleId)
                .ne(BlogFrontComment::getStatus, "2")
                .count();

        // 更新文章的评论数
        UpdateWrapper<BlogFrontVO> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", articleId)
                .set("comment_count", commentCount)
                .setSql("update_time = now()");

        frontMapper.update(null, wrapper);
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return true-点赞成功，false-点赞失败
     */
    @Override
    public boolean likeComment(String commentId) {
        UpdateWrapper<BlogFrontComment> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", commentId)
                .setSql("like_count = like_count + 1")
                .setSql("update_time = now()");
        // 执行点赞数+1操作
        return commentMapper.update(null, wrapper) > 0;
    }

}