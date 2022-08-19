package com.ruoyi.pension.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.po.PensionPaymentNotify;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.common.mapper.PensionPaymentNotifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【pension_payment_notify】的数据库操作Service
* @createDate 2022-07-05 09:49:08
*/
@Service
public class PensionPaymentNotifyService extends ServiceImpl<PensionPaymentNotifyMapper, PensionPaymentNotify> implements IService<PensionPaymentNotify> {
    @Autowired
    private RedisCache redisCache;
    public void saveAndCache(PensionPaymentNotify pensionPaymentNotify){
        //先缓存
        redisCache.setCacheObject(PensionBusiness.getKeyOfPayment(pensionPaymentNotify),pensionPaymentNotify,3, TimeUnit.MINUTES);
        //再保存
        save(pensionPaymentNotify);
    }
    public PensionPaymentNotify getOrCacheByExample(PensionPaymentNotify pensionPaymentNotify){
        //先尝试从缓存获取
        PensionPaymentNotify record = redisCache.getCacheObject(PensionBusiness.getKeyOfPayment(pensionPaymentNotify));
        if(record != null) return record;
        //没有则从数据库查找
        return this.baseMapper.getByExample(pensionPaymentNotify);
    }
}
