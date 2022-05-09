
import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

export function listDevice(query) {
  return request({
    url: '/device/device/list',
    method: 'get',
    params: query
  })
}

// 查询设备详细
export function getDevice(id) {
  return request({
    url: '/device/device/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 修改设备
export function updateDevice(data) {
  return request({
    url: '/device/device',
    method: 'patch',
    data: data
  })
}

// 删除上报日志
export function delDevice(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/device/device/batch?id='+id,
    method: 'delete'
  })
}
