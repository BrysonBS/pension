package com.ruoyi.pension.common.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.domain.po.PensionRefundWechat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.common.mapper.PensionRefundWechatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author Administrator
* @description 针对表【pension_refund_wechat】的数据库操作Service
* @createDate 2022-08-11 15:57:56
*/
@Service
public class PensionRefundWechatService extends ServiceImpl<PensionRefundWechatMapper, PensionRefundWechat> implements IService<PensionRefundWechat> {
    @Transactional
    public boolean saveIfAbsent(PensionRefundWechat wechat){
        return this.baseMapper.getCountByRefundIdAndOutTradeNo(wechat.getRefundId(), wechat.getOutTradeNo()) == 0 ?
            super.save(wechat) : updateByRefundIdAndOutTradeNo(wechat);
    }
    @Transactional
    public boolean updateByRefundIdAndOutTradeNo(PensionRefundWechat refundWechat){
        LambdaUpdateWrapper<PensionRefundWechat> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PensionRefundWechat::getRefundId,refundWechat.getRefundId())
                .eq(PensionRefundWechat::getOutTradeNo,refundWechat.getOutTradeNo())
                .set(PensionRefundWechat::getTransactionId,refundWechat.getTransactionId())
                .set(PensionRefundWechat::getOutRefundNo,refundWechat.getOutRefundNo())
                .set(PensionRefundWechat::getRefundStatus,refundWechat.getRefundStatus())
                .set(PensionRefundWechat::getSuccessTime,refundWechat.getSuccessTime())
                .set(PensionRefundWechat::getTotal,refundWechat.getTotal())
                .set(PensionRefundWechat::getRefund,refundWechat.getRefund())
                .set(PensionRefundWechat::getPayerTotal,refundWechat.getPayerTotal())
                .set(PensionRefundWechat::getPayerRefund,refundWechat.getPayerRefund())
                .set(PensionRefundWechat::getUserReceivedAccount,refundWechat.getUserReceivedAccount());
        return super.update(updateWrapper);
    }

}
