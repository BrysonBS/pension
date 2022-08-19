
import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

//获取当前配置
export function getOrderSetting() {
  return request({
    url: '/nursing/order/setting',
    method: 'get'
  })
}
//添加或者更新
export function saveOrUpdateOrderSetting(data) {
  return request({
    url: '/nursing/order/setting',
    method: 'patch',
    data: data
  })
}
