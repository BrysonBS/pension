//查询
import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

//列表
export function listNurisngServicePrice(query) {
  return request({
    url: '/nursing/service/price/list',
    method: 'get',
    params: query
  })
}
//当前机构下列表
export function listNursingServicePriceOwn(query){
  return request({
    url: '/nursing/service/price/listOwn',
    method: 'get',
    params: query
  })
}

// 根据id查询
export function getNursingServicePrice(id) {
  return request({
    url: '/nursing/service/price/'+parseStrEmpty(id),
    method: 'get'
  })
}
// 新增
export function addNursingServicePrice(data) {
  return request({
    url: '/nursing/service/price',
    method: 'post',
    data: data
  })
}
// 修改
export function updateNursingServicePrice(data) {
  return request({
    url: '/nursing/service/price',
    method: 'put',
    data: data
  })
}
// 删除
export function delNursingServicePrice(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/nursing/service/price/batch?id='+id,
    method: 'delete'
  })
}
