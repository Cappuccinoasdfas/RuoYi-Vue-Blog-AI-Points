// src/views/blog/api/comment.js
import request from '@/utils/request'

/**
 * 获取文章评论列表
 */
export function getComments(articleId, query) {
  return request({
    url: `/blog/comment/list/${articleId}`,
    method: 'get',
    params: query
  })
}

/**
 * 获取更多子评论
 */
export function getChildComments(parentId, params) {
  return request({
    url: `/blog/comment/children/${parentId}`,
    method: 'get',
    params: params
  })
}

/**
 * 发表评论
 */
export function addComment(data) {
  return request({
    url: '/blog/comment/add',
    method: 'post',
    data: data
  })
}

/**
 * 点赞评论
 */
export function likeComment(commentId) {
  return request({
    url: `/blog/comment/like/${commentId}`,
    method: 'post'
  })
}

/**
 * 删除评论
 */
export function deleteComment(commentId) {
  return request({
    url: `/blog/comment/delete/${commentId}`,
    method: 'delete'
  })
}