import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

//列表
export function listWorker(query) {
  return request({
    url: '/nursing/worker/list',
    method: 'get',
    params: query
  })
}
// 根据id查询
export function getWorker(id) {
  return request({
    url: '/nursing/worker/'+parseStrEmpty(id),
    method: 'get'
  })
}
//新增
export function addWorker(data) {
  return request({
    url: '/nursing/worker',
    method: 'post',
    params: data
  })
}
// 修改
export function updateWorker(data) {
  return request({
    url: '/nursing/worker',
    method: 'put',
    data: data
  })
}
// 删除
export function delWorker(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/nursing/worker/batch?id='+id,
    method: 'delete'
  })
}
// 根据id查询证书列表
export function getWorkerCertificate(id) {
  return request({
    url: '/nursing/worker/'+parseStrEmpty(id)+"/certificate",
    method: 'get'
  })
}
//新增指定护工的证书
export function addWorkerCertificate(id,data) {
  return request({
    headers:{'Content-Type': 'multipart/form-data',repeatSubmit:false},
    url: '/nursing/worker/'+parseStrEmpty(id)+"/certificate",
    method: 'post',
    data: data
  })
}
//删除指定护工的证书
export function delWorkerCertificate(id) {
  return request({
    url: '/nursing/worker/certificate/'+parseStrEmpty(id),
    method: 'delete'
  })
}


