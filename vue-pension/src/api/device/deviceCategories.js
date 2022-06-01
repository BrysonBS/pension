import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

export function listDeviceCategories() {
  return request({
    url: '/device/categories/list',
    method: 'get'
  })
}
export function deviceList() {
  return request({
    url: '/device/categories/deviceList',
    method: 'get'
  })
}
export function gwList() {
  return request({
    url: '/device/categories/gwList',
    method: 'get'
  })
}
export function listBiolandDeviceCategories() {
  return request({
    url: '/device/bioland/categories/typeList',
    method: 'get'
  })
}
export function biolandDeviceList(query){
  return request({
    url: '/device/bioland/blpressureList',
    method: 'get',
    params: query
  })
}

