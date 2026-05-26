import request from '@/utils/request'

// 查询AI聊天会话列表
export function listSession(query) {
  return request({
    url: '/ai_chat_session/session/list',
    method: 'get',
    params: query
  })
}

// 查询AI聊天会话详细
export function getSession(sessionId) {
  return request({
    url: '/ai_chat_session/session/' + sessionId,
    method: 'get'
  })
}

// 新增AI聊天会话
export function addSession(data) {
  return request({
    url: '/ai_chat_session/session',
    method: 'post',
    data: data
  })
}

// 修改AI聊天会话
export function updateSession(data) {
  return request({
    url: '/ai_chat_session/session',
    method: 'put',
    data: data
  })
}

// 删除AI聊天会话
export function delSession(sessionId) {
  return request({
    url: '/ai_chat_session/session/' + sessionId,
    method: 'delete'
  })
}
