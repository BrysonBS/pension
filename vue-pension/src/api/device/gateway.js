import request from '@/utils/request'
import { parseStrEmpty, tansParams } from '@/utils/ruoyi'

// 查询网关列表
export function listGateway(query) {
  return request({
    url: '/device/gateway/list',
    method: 'get',
    params: query
  })
}
// 新增网关
export function addGateway(data) {
  return request({
    url: '/device/gateway',
    method: 'post',
    data: data
  })
}

// 根据id查询网关
export function getGateway(id) {
  return request({
    url: '/device/gateway/'+parseStrEmpty(id),
    method: 'get'
  })
}

// 修改网关
export function updateGateway(data) {
  return request({
    url: '/device/gateway',
    method: 'put',
    data: data
  })
}
// 删除网关
export function delGateway(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/device/gateway/batch?id='+id,
    method: 'delete'
  })
}
// 用户状态修改
export function changeGatewayStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/device/gateway/changeStatus',
    method: 'patch',
    data: data
  })
}
