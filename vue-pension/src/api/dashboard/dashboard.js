import request from '@/utils/request'

export function orderAmount() {
  return request({
    url: '/dashboard/order/amount',
    method: 'get',
  })
}
export function orderStatus() {
  return request({
    url: '/dashboard/order/status',
    method: 'get',
  })
}
export function orderDay() {
  return request({
    url: '/dashboard/order/day',
    method: 'get',
  })
}
export function nursingCount() {
  return request({
    url: '/dashboard/nursing/count',
    method: 'get',
  })
}
export function deviceLatest(query) {
  return request({
    url: '/dashboard/device/latest',
    method: 'get',
    params: query,
  })
}
