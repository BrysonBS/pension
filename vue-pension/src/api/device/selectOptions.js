import request from '@/utils/request'

//账号列表
export function getUserOptions(query) {
  return request({
    url: '/select/options/user',
    method: 'get',
    params: query
  })
}
