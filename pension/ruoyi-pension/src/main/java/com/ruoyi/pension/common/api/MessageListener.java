package com.ruoyi.pension.common.api;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.pension.nursing.service.NursingOrderService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    /**
     * 关闭未支付订单
     * @param orderId
     */
    @JmsListener(destination = "${message-queue.nursing-order-close}")
    public void receiveNursingOrderClose(Integer orderId){
        SpringUtils.getBean(NursingOrderService.class).closeOrderIfUnpaid(orderId);
    }

    /**
     * 支付完成超时自动完成5星好评
     * @param orderId
     */
    @JmsListener(destination = "${message-queue.nursing-order-complete}")
    public void receiveNursingOrderComplete(Integer orderId){
        SpringUtils.getBean(NursingOrderService.class).autoCommentOrderIfComplete(orderId);
    }
}
