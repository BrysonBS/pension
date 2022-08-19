package com.ruoyi.pension.common.service;

import java.math.BigDecimal;

public interface BaseOrderService {
    /** 支付完成更新状态为已支付 */
    void updateOrderStatusToPaidByOrderNo(String outTradeNo);
    void updateOrderRefundAmountByOrderNo(String outTradeNo,BigDecimal refundAmount);
}
