import request from '@/utils/request'

// 查询用户购买记录列表
export function listPurchase(query) {
  return request({
    url: '/ai_user_purchase/purchase/list',
    method: 'get',
    params: query
  })
}

// 查询用户购买记录详细
export function getPurchase(purchaseId) {
  return request({
    url: '/ai_user_purchase/purchase/' + purchaseId,
    method: 'get'
  })
}

// 新增用户购买记录
export function addPurchase(data) {
  return request({
    url: '/ai_user_purchase/purchase',
    method: 'post',
    data: data
  })
}

// 修改用户购买记录
export function updatePurchase(data) {
  return request({
    url: '/ai_user_purchase/purchase',
    method: 'put',
    data: data
  })
}

// 删除用户购买记录
export function delPurchase(purchaseId) {
  return request({
    url: '/ai_user_purchase/purchase/' + purchaseId,
    method: 'delete'
  })
}
