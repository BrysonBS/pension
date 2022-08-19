// 支付测试
import request from '@/utils/request'
import axios from 'axios'
import { getToken } from '@/utils/auth'
import { parseStrEmpty } from '@/utils/ruoyi'

//列表
export function listNurisngOrder(query) {
  return request({
    url: '/nursing/order/list',
    method: 'get',
    params: query
  })
}
//护理项目列表
export function listNursingOrderItems(query){
  return request({
    url: '/nursing/order/items/listByOrderId',
    method: 'get',
    params: query
  })
}
//下单
export function addOrder(data) {
  return request({
    url: '/nursing/order',
    method: 'post',
    data: data
  })
}
//派单
export function deliveryOrder(data) {
  return request({
    url: '/nursing/order/delivery',
    method: 'patch',
    data: data
  })
}
//接单
export function receiveOrder(data) {
  return request({
    url: '/nursing/order/receive',
    method: 'patch',
    data: data
  })
}
//结单
export function completeOrder(data) {
  return request({
    url: '/nursing/order/complete',
    method: 'patch',
    data: data
  })
}
// 删除
export function delNursingOrder(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/nursing/order/batch?id='+id,
    method: 'delete'
  })
}

// 退款
export function refundOrder(data) {
  return request({
    url: '/nursing/order/refund',
    method: 'post',
    data: data
  })
}

