// 查询博客文章列表
export function listArticle(query) {
  return request({
    url: '/blog_article/user/getUser',
    method: 'get',
    // params: query 
  })
}