import request from '@/utils/request'
import { getToken } from '@/utils/auth'

export function RunAwayBotton(){
    return request({
        url: `/runAway/run`,
        method: 'post'
    })
}

// 查询API接口文档列表
export function listApiDoc(query) {
  return request({
    url: '/apiDoc/doc/list',
    method: 'get',
    params: query
  })
}

// 查询API接口文档详细
export function getApiDoc(id) {
  return request({
    url: '/apiDoc/doc/' + id,
    method: 'get'
  })
}

// AI 聊天接口
export function aiChat(data) {
  return request({
    url: '/chat/send',
    method: 'post',
    data: data
  })
}

// 加载会话列表
export const loadSession = () => {
  return request({
    url: '/chat/loadSession',
    method: 'get'
  })
}

// 获取指定会话的聊天记录
export const getSessionChat = (sessionId) => {
  return request({
    url: '/chat/getSession',
    method: 'get',
    params: { sessionId }
  })
}

// 删除会话
export const deleteSession = (sessionId) => {
  return request({
    url: `/chat/deleteSession/${sessionId}`,
    method: 'delete'
  })
}

// ========== 流式接口：使用环境变量 ==========
export function aiChatStream(data, onMessage, onError, onDone) {
  const token = getToken()
  const baseURL = process.env.VUE_APP_BASE_API  // ← 从环境变量读取
  
  return fetch(`${baseURL}/chat/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(data)
  }).then(response => {
    if (!response.ok) {
      onError && onError('请求失败')
      return
    }
    
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    
    function read() {
      reader.read().then(({ done, value }) => {
        if (done) {
          onDone && onDone()
          return
        }
        
        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''
        
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const data = line.substring(5).trim()
            if (data) {
              onMessage && onMessage(data)
            }
          } else if (line.startsWith('event:done')) {
            onDone && onDone()
          } else if (line.startsWith('event:error')) {
            onError && onError('服务异常')
          }
        }
        
        read()
      })
    }
    
    read()
  })
}