package com.ruoyi.pension.common.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.common.domain.po.PensionPaymentNotifyWechat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【pension_payment_notify_wechat】的数据库操作Mapper
* @createDate 2022-07-22 10:34:14
* @Entity com.ruoyi.pension.common.domain.po.PensionPaymentNotifyWechat
*/
public interface PensionPaymentNotifyWechatMapper extends BaseMapper<PensionPaymentNotifyWechat> {
    Integer getCountByNotifyIdAndOutTradeNo(@Param("notifyId") String notifyId,@Param("outTradeNo") String outTradeNo);
}




