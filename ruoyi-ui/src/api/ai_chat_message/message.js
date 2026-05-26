import request from '@/utils/request'

// 查询AI聊天消息列表
export function listMessage(query) {
  return request({
    url: '/ai_chat_message/message/list',
    method: 'get',
    params: query
  })
}

// 查询AI聊天消息详细
export function getMessage(messageId) {
  return request({
    url: '/ai_chat_message/message/' + messageId,
    method: 'get'
  })
}

// 新增AI聊天消息
export function addMessage(data) {
  return request({
    url: '/ai_chat_message/message',
    method: 'post',
    data: data
  })
}

// 修改AI聊天消息
export function updateMessage(data) {
  return request({
    url: '/ai_chat_message/message',
    method: 'put',
    data: data
  })
}

// 删除AI聊天消息
export function delMessage(messageId) {
  return request({
    url: '/ai_chat_message/message/' + messageId,
    method: 'delete'
  })
}
