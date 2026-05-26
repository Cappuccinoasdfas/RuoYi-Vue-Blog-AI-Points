import request from '@/utils/request'

/**
 * 博客文章API接口
 */

// ==================== 查询接口 ====================

/**
 * 查询博客文章列表（分页）
 * @param {Object} query - 查询参数
 * @param {Number} query.pageNum - 页码
 * @param {Number} query.pageSize - 每页条数
 * @param {String} query.status - 文章状态（0发布 1草稿）
 * @param {String} query.category - 分类代码
 * @param {Long} query.userId - 作者ID
 * @returns {Promise}
 */
export function listArticle(query) {
  return request({
    url: '/blog/article/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取文章详情（会自增浏览量）
 * @param {Number} id - 文章ID
 * @returns {Promise}
 */
export function getArticleDetail(id) {
  return request({
    url: `/blog/article/detail/${id}`,
    method: 'get'
  })
}

/**
 * 数据统计
 * 
*/
export function getStatistics(){
  return request({
    url: '/blog/article/statistics',
    method: 'get'
  })
}

/**
 * 获取当前用户的文章列表（包括草稿）
 * @param {Object} query - 查询参数
 * @returns {Promise}
 */
export function getMyArticles(query) {
  return request({
    url: '/blog/article/my',
    method: 'get',
    params: query
  })
}


// ==================== 操作接口（需要登录） ====================

/**
 * 发布/保存文章
 * @param {Object} data - 文章数据
 * @param {String} data.title - 标题
 * @param {String} data.content - 内容
 * @param {String} data.summary - 摘要
 * @param {String} data.cover - 封面图
 * @param {String} data.category - 分类代码
 * @param {String} data.categoryName - 分类名称
 * @param {String} data.status - 状态（0发布 1草稿）
 * @returns {Promise}
 */
export function saveArticle(data) {
  return request({
    url: '/blog/article/save',
    method: 'post',
    data: data
  })
}

/**
 * 更新文章
 * @param {Object} data - 文章数据
 * @returns {Promise}
 */
export function updateArticle(data) {
  return request({
    url: '/blog/article/update',
    method: 'put',
    data: data
  })
}

/**
 * 删除文章（逻辑删除）
 * @param {Number|Array} ids - 文章ID或ID数组
 * @returns {Promise}
 */
export function deleteArticle(ids) {
  return request({
    url: `/blog/article/delete/${ids}`,
    method: 'delete'
  })
}


/**
 * 点赞/取消点赞文章
 * @param {Number} id - 文章ID
 * @returns {Promise}
 */
export function likeArticle(id) {
  return request({
    url: `/blog/article/toggleLike/${id}`,
    method: 'put'
  })
}

// ==================== 评论相关（预留接口） ====================

/**
 * 获取文章评论列表
 * @param {Number} articleId - 文章ID
 * @param {Object} query - 分页参数
 * @returns {Promise}
 */
export function getComments(articleId, query) {
  return request({
    url: `/blog/comment/list/${articleId}`,
    method: 'get',
    params: query
  })
}

/**
 * 发表评论
 * @param {Object} data - 评论数据
 * @param {Number} data.articleId - 文章ID
 * @param {String} data.content - 评论内容
 * @param {Number} data.parentId - 父评论ID（回复评论时使用）
 * @returns {Promise}
 */
export function addComment(data) {
  return request({
    url: '/blog/comment/add',
    method: 'post',
    data: data
  })
}



// ==================== 用户统计接口 ====================

/**
 * 获取用户统计数据（文章数、浏览数、点赞数）
 * @param {Number} userId - 用户ID，不传则查询当前用户
 * @returns {Promise}
 */
export function getUserStats(userId) {
  return request({
    url: '/blog/user/stats',
    method: 'get',
    params: { userId }
  })
}

/**
 * 上传文章图片（富文本编辑器用）
 * @param {FormData} data - 图片文件
 * @returns {Promise}
 */
export function uploadArticleImage(data) {
  return request({
    url: '/blog/article/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}