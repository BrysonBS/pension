import request from '@/utils/request'


// 查询上报日志列表
export function list(query) {
  return request({
    url: '/device/notice/list',
    method: 'get',
    params: query
  })
}
// 删除上报日志
export function delNotice(id) {
  if(id instanceof Array) id = id.join('&id=')
  return request({
    url: '/device/notice/batch?id='+id,
    method: 'delete'
  })
}
