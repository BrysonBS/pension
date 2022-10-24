//设备列表
import request from '@/utils/request'

export function list(query) {
  return request({
    url: '/tuya/device/list',
    method: 'get',
    params: query
  })
}
// 查询设备详细
export function getById(id) {
  return request({
    url: '/tuya/device/byId/' + id,
    method: 'get'
  })
}
// 修改设备和手机号
export function updateTuyaDevice(data) {
  return request({
    url: '/tuya/device/',
    method: 'patch',
    data: data
  })
}
// 删除设备
export function delTuyaDevice(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/tuya/device/batch?id='+id,
    method: 'delete'
  })
}
// 刷新字典缓存
export function refresh() {
  return request({
    url: '/tuya/device/refresh',
    method: 'get'
  })
}
