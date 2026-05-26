import request from '@/utils/request'

/**
 * 获取商品列表
 */
export function getProductList() {
  return request({
    url: '/ai_points_product/product/list',
    method: 'get'
  })
}

/**
 * 获取用户已购买的商品ID列表
 */
export function getPurchasedProducts() {
  return request({
    url: '/points/getProducts',
    method: 'get'
  })
}

/**
 * 购买商品（扣积分）
 * @param {number} productId 商品ID
 */
export function purchaseProduct(productId) {
  return request({
    url: '/points/purchase',
    method: 'post',
    data: productId 
  })
}

//获取充值套餐列表
export function getRechargePackages() {
  return request({
    url: '/ai_recharge_package/package/list',
    method: 'get'
  })
}

/**
 * 获取充值记录
 */
export function getRechargeOrders(params) {
  return request({
    url: '/ai_recharge_package/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户当前积分
 * 
  */
export function getUserPoints() {
  return request({
    url: '/points/getPoints',
    method: 'get'
  })
}