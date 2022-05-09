import request from '@/utils/request'

export function listSleep(query) {
  return request({
    url: '/health/sleep',
    method: 'get',
    params: query
  })
}
