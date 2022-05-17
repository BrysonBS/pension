import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

//设备列表
export function listBiolandDevice(query) {
  return request({
    url: '/device/bioland/list',
    method: 'get',
    params: query
  })
}
// 查询设备详细
export function getBiolandDevice(id) {
  return request({
    url: '/device/bioland/' + parseStrEmpty(id),
    method: 'get'
  })
}
// 删除设备
export function delBiolandDevice(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/device/bioland/batch?id='+id,
    method: 'delete'
  })
}
// 修改设备和手机号
export function updateBiolandDevice(data) {
  return request({
    url: '/device/bioland',
    method: 'patch',
    data: data
  })
}
