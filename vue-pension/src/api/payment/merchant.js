//新增
import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

//列表
export function listMerchant(query) {
  return request({
    url: '/payment/merchant/list',
    method: 'get',
    params: query
  })
}
// 根据id查询
export function getMerchant(id) {
  return request({
    url: '/payment/merchant/'+parseStrEmpty(id),
    method: 'get'
  })
}
//根据支付类型查询
export function getMerchantByPayType(query) {
  return request({
    url: '/payment/merchant/payType',
    method: 'get',
    params: query
  })
}
//添加
export function addMerchant(data) {
  return request({
    headers:{'Content-Type':'multipart/form-data'},
    url: '/payment/merchant/add',
    method: 'post',
    data: data
  })
}
//修改
export function updateMerchant(data) {
  return request({
    headers:{'Content-Type':'multipart/form-data'},
    url: '/payment/merchant/update',
    method: 'post',
    data: data
  })
}
// 删除
export function delMerchant(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/payment/merchant/batch?id='+id,
    method: 'delete'
  })
}
// 状态修改
export function changeMerchantStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/payment/merchant/changeStatus',
    method: 'patch',
    data: data
  })
}

//刷新缓存
export function refreshMerchantCache() {
  return request({
    url: '/payment/merchant/refresh',
    method: 'delete'
  })
}
