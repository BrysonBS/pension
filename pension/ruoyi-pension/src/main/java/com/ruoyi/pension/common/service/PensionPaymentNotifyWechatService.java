package com.ruoyi.pension.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.domain.po.PensionPaymentNotifyWechat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.common.mapper.PensionPaymentNotifyWechatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author Administrator
* @description 针对表【pension_payment_notify_wechat】的数据库操作Service
* @createDate 2022-07-22 10:34:14
*/
@Service
public class PensionPaymentNotifyWechatService extends ServiceImpl<PensionPaymentNotifyWechatMapper, PensionPaymentNotifyWechat> implements IService<PensionPaymentNotifyWechat> {
    @Transactional
    public boolean saveIfAbsent(PensionPaymentNotifyWechat wechatNotify){
        return this.baseMapper.getCountByNotifyIdAndOutTradeNo(wechatNotify.getNotifyId(), wechatNotify.getOutTradeNo()) != 0 || super.save(wechatNotify);
    }
}
