package com.ruoyi.blog_comment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.blog_comment.domain.BlogFrontComment;
import com.ruoyi.blog_comment.service.IBlogFrontCommentService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog/comment")
public class BlogFrontCommentController extends BaseController {

    @Autowired
    private IBlogFrontCommentService commentService;

    /**
     * 获取文章评论列表
     * @param articleId 文章ID
     * @ return 评论列表
     */
    @GetMapping("/list/{articleId}")
//    @PreAuthorize("isAuthenticated()")
    @Anonymous
    public AjaxResult list(@PathVariable Long articleId,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        AjaxResult ajax = AjaxResult.success();
        IPage<BlogFrontComment> page =  commentService.getCommentPage(pageNum, pageSize, articleId);
        //查询评论总数
        Long totalCommentCount = commentService.getTotalCommentCount(articleId);
        return ajax.success()
                .put("data", page.getRecords())
                .put("total", page.getTotal())
                .put("totalPages", page.getPages())
                .put("totalCommentCount", totalCommentCount);
    }

    /**
     * 获取更多子评论
     */
    @GetMapping("/children/{parentId}")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult getChildren(@PathVariable String parentId,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "5") Integer pageSize) {
        System.out.println("parentId: " + parentId + ", pageNum: " + pageNum + ", pageSize: " + pageSize);
        List<BlogFrontComment> children = commentService.getMoreChildComments(parentId, pageNum, pageSize);
        return AjaxResult.success(children);
    }

    /**
     * 发表评论
     */
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult add(@RequestBody BlogFrontComment comment) {
        try {
            BlogFrontComment savedComment = commentService.addComment(comment);
            //
            return AjaxResult.success("评论成功", savedComment);
        } catch (Exception e) {
            return AjaxResult.error("评论失败：" + e.getMessage());
        }
    }

    /**
     * 点赞评论
     */
    @PostMapping("/like/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult like(@PathVariable String commentId) {
        boolean success = commentService.likeComment(commentId);
        return success ? AjaxResult.success("点赞成功") : AjaxResult.error("点赞失败");
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult delete(@PathVariable String commentId) {
        Long userId = SecurityUtils.getUserId();
        boolean success = commentService.deleteComment(commentId, userId);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

}