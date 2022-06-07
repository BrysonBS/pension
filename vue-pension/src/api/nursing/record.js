import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

//新增初始化
export function addRecordInit() {
  return request({
    url: '/nursing/record/init',
    method: 'get'
  })
}

//查询
export function listRecord(query) {
  return request({
    url: '/nursing/record/listRecord',
    method: 'get',
    params: query
  })
}
//查询子表详细信息
export function getDetail(recordId) {
  return request({
    url: '/nursing/record/detail/' + parseStrEmpty(recordId),
    method: 'get',
  })
}

// 新增
export function addRecord(data) {
  return request({
    headers:{'Content-Type': 'multipart/form-data'},
    url: '/nursing/record',
    method: 'post',
    data: data
  })
}

// 删除
export function delRecord(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/nursing/record/batch?id='+id,
    method: 'delete'
  })
}
