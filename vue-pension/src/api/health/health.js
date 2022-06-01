import request from '@/utils/request'

export function listSleep(query) {
  return request({
    url: '/health/sleep',
    method: 'get',
    params: query
  })
}
export function listBlpressure(query) {
  return request({
    url: '/bioland/record/data',
    method: 'get',
    params: query
  })
}
