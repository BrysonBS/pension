package com.ruoyi.pension.common.message;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.pension.common.domain.message.MobileMessage;
import com.ruoyi.pension.nursing.service.NursingOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
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
    @JmsListener(destination = "${message-queue.common-sms}")
    public void receiveSms(MobileMessage message){
        //SendSms.sendWarning();
    }
    @JmsListener(destination = "${message-queue.common-vms}")
    public void receiveVms(){

    }
    @JmsListener(destination = "${message-queue.owon-device-report}")
    public void receiveOwonDeviceReport(){

    }
    @JmsListener(destination = "${message-queue.bioland-device-report}")
    public void receiveBiolandDeviceReport(){

    }
}
