import request from '@/utils/request'

// 查询充值订单列表
export function listOrder(query) {
  return request({
    url: '/ai_recharge_order/order/list',
    method: 'get',
    params: query
  })
}

// 查询充值订单详细
export function getOrder(orderId) {
  return request({
    url: '/ai_recharge_order/order/' + orderId,
    method: 'get'
  })
}

// 新增充值订单
export function addOrder(data) {
  return request({
    url: '/ai_recharge_order/order',
    method: 'post',
    data: data
  })
}

// 修改充值订单
export function updateOrder(data) {
  return request({
    url: '/ai_recharge_order/order',
    method: 'put',
    data: data
  })
}

// 删除充值订单
export function delOrder(orderId) {
  return request({
    url: '/ai_recharge_order/order/' + orderId,
    method: 'delete'
  })
}
