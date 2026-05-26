import request from '@/utils/request'

// 查询积分商品列表
export function listProduct(query) {
  return request({
    url: '/ai_points_product/product/list',
    method: 'get',
    params: query
  })
}

// 查询积分商品详细
export function getProduct(productId) {
  return request({
    url: '/ai_points_product/product/' + productId,
    method: 'get'
  })
}

// 新增积分商品
export function addProduct(data) {
  return request({
    url: '/ai_points_product/product',
    method: 'post',
    data: data
  })
}

// 修改积分商品
export function updateProduct(data) {
  return request({
    url: '/ai_points_product/product',
    method: 'put',
    data: data
  })
}

// 删除积分商品
export function delProduct(productId) {
  return request({
    url: '/ai_points_product/product/' + productId,
    method: 'delete'
  })
}
