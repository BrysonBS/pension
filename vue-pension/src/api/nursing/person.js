import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

export function listPerson(query) {
  return request({
    url: '/nursing/person/list',
    method: 'get',
    params: query
  })
}
export function listPersonAll() {
  return request({
    url: '/nursing/person/listAll',
    method: 'get'
  })
}
// 根据id查询
export function getPerson(id) {
  return request({
    url: '/nursing/person/'+parseStrEmpty(id),
    method: 'get'
  })
}
export function addPerson(data) {
  return request({
    url: '/nursing/person',
    method: 'post',
    params: data
  })
}
// 修改
export function updatePerson(data) {
  return request({
    url: '/nursing/person',
    method: 'put',
    data: data
  })
}
// 删除
export function delPerson(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/nursing/person/batch?id='+id,
    method: 'delete'
  })
}

