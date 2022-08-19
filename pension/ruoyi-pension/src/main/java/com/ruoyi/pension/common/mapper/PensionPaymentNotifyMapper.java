package com.ruoyi.pension.common.mapper;

import com.ruoyi.pension.common.domain.po.PensionPaymentNotify;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【pension_payment_notify】的数据库操作Mapper
* @createDate 2022-07-05 09:49:08
* @Entity com.ruoyi.pension.common.domain.po.PensionPaymentNotify
*/
public interface PensionPaymentNotifyMapper extends BaseMapper<PensionPaymentNotify> {
    PensionPaymentNotify getByExample(PensionPaymentNotify pensionPaymentNotify);
}




