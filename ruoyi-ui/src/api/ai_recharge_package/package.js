import request from '@/utils/request'

// 查询充值套餐列表
export function listPackage(query) {
  return request({
    url: '/ai_recharge_package/package/list',
    method: 'get',
    params: query
  })
}

// 查询充值套餐详细
export function getPackage(packageId) {
  return request({
    url: '/ai_recharge_package/package/' + packageId,
    method: 'get'
  })
}

// 新增充值套餐
export function addPackage(data) {
  return request({
    url: '/ai_recharge_package/package',
    method: 'post',
    data: data
  })
}

// 修改充值套餐
export function updatePackage(data) {
  return request({
    url: '/ai_recharge_package/package',
    method: 'put',
    data: data
  })
}

// 删除充值套餐
export function delPackage(packageId) {
  return request({
    url: '/ai_recharge_package/package/' + packageId,
    method: 'delete'
  })
}
