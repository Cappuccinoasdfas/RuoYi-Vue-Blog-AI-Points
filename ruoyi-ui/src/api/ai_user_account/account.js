import request from '@/utils/request'

// 查询用户积分账户列表
export function listAccount(query) {
  return request({
    url: '/ai_user_account/account/list',
    method: 'get',
    params: query
  })
}

// 查询用户积分账户详细
export function getAccount(accountId) {
  return request({
    url: '/ai_user_account/account/' + accountId,
    method: 'get'
  })
}

// 新增用户积分账户
export function addAccount(data) {
  return request({
    url: '/ai_user_account/account',
    method: 'post',
    data: data
  })
}

// 修改用户积分账户
export function updateAccount(data) {
  return request({
    url: '/ai_user_account/account',
    method: 'put',
    data: data
  })
}

// 删除用户积分账户
export function delAccount(accountId) {
  return request({
    url: '/ai_user_account/account/' + accountId,
    method: 'delete'
  })
}
