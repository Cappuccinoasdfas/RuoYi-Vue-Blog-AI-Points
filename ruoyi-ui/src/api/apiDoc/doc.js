import request from '@/utils/request'

// 查询API接口文档列表
export function listDoc(query) {
  return request({
    url: '/apiDoc/doc/list',
    method: 'get',
    params: query
  })
}

// 查询API接口文档详细
export function getDoc(id) {
  return request({
    url: '/apiDoc/doc/' + id,
    method: 'get'
  })
}

// 新增API接口文档
export function addDoc(data) {
  return request({
    url: '/apiDoc/doc',
    method: 'post',
    data: data
  })
}

// 修改API接口文档
export function updateDoc(data) {
  return request({
    url: '/apiDoc/doc',
    method: 'put',
    data: data
  })
}

// 删除API接口文档
export function delDoc(id) {
  return request({
    url: '/apiDoc/doc/' + id,
    method: 'delete'
  })
}
