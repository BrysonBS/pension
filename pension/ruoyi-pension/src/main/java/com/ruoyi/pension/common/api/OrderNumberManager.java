package com.ruoyi.pension.common.api;

import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class OrderNumberManager {

    @Autowired
    private RedisTemplate redisTemplate;

    /** 获取微信支付订单号 */
    public String getWeChatPayOrderNumber(Long deptId){
        return getOrderNumber(PensionBusiness.PAYMENT_ORDER_PREFIX + "2" + deptId);
    }
    /** 获取支付宝支付订单号 */
    public String getAlipayOrderNumber(Long deptId){
        return getOrderNumber(PensionBusiness.PAYMENT_ORDER_PREFIX + "1" + deptId);
    }
    public String getRefundOrderNumber(String orderNumber){
        return getOrderNumber(orderNumber,3);
    }

    public String getOrderNumber(String prefix){
        return getOrderNumber(prefix,9);
    }
    public String getOrderNumber(String prefix,int length){
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return getAndAppendKeyPrefix(prefix + date,length,25,TimeUnit.HOURS);
    }
    private String getAndAppendKeyPrefix(String key,int length,long timeOut, TimeUnit timeUnit){
        long incr = increment(key,timeOut,timeUnit);
        return key + String.format("%0" + length + "d",incr);
    }
    private long increment(String key, long timeOut, TimeUnit timeUnit){
        //构造函数: 存在就获取,不存在就新建且值为0
        RedisAtomicLong atomicLong = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        long increment = atomicLong.getAndIncrement();
        if(increment == 0) {
            atomicLong.expire(timeOut, timeUnit);//新创建则设置过期时间
            increment = atomicLong.getAndIncrement();//从1开始
        }
        return increment;
    }
}
