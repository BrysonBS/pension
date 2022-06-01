package com.ruoyi.pension.common.api;

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

    public String getOrderNumber(String key){
        return getOrderNumber(key,6);
    }
    public String getOrderNumber(String key,int length){
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return getAndAppendKeyPrefix(key + date,length,25,TimeUnit.HOURS);
    }
    public String getAndAppendKeyPrefix(String key,int length,long timeOut, TimeUnit timeUnit){
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
