import request from '@/utils/request'

// 查询AI性格列表
export function listPersonality(query) {
  return request({
    url: '/ai_personality/personality/list',
    method: 'get',
    params: query
  })
}

// 查询AI性格详细
export function getPersonality(personalityId) {
  return request({
    url: '/ai_personality/personality/' + personalityId,
    method: 'get'
  })
}

// 新增AI性格
export function addPersonality(data) {
  return request({
    url: '/ai_personality/personality',
    method: 'post',
    data: data
  })
}

// 修改AI性格
export function updatePersonality(data) {
  return request({
    url: '/ai_personality/personality',
    method: 'put',
    data: data
  })
}

// 删除AI性格
export function delPersonality(personalityId) {
  return request({
    url: '/ai_personality/personality/' + personalityId,
    method: 'delete'
  })
}
