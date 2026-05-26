import request from '@/utils/request'

/**
 * 获取用户详细信息
 */
export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get'
  })
}

/**
 * 修改用户个人信息
 */
export function updateProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data: data
  })
}

/**
 * 修改密码
 */
export function updatePassword(data) {
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data: data
  })
}

/**
 * 上传头像
 */
export function updateAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}