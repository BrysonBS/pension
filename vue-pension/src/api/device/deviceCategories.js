import request from '@/utils/request'

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
