package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.service.BaseOrderService;
import com.ruoyi.pension.nursing.domain.po.NursingOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.domain.po.NursingOrderItems;
import com.ruoyi.pension.nursing.domain.po.NursingOrderSetting;
import com.ruoyi.pension.nursing.domain.po.NursingWorker;
import com.ruoyi.pension.nursing.mapper.NursingOrderMapper;
import org.apache.activemq.artemis.api.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
* @author Administrator
* @description 针对表【nursing_order】的数据库操作Service
* @createDate 2022-07-30 15:39:43
*/
@Service
public class NursingOrderService extends ServiceImpl<NursingOrderMapper, NursingOrder> implements IService<NursingOrder>, BaseOrderService {
    @Autowired
    private NursingOrderItemsService nursingOrderItemsService;
    @Autowired
    private NursingWorkerService nursingWorkerService;
    @Autowired
    private NursingOrderSettingService nursingOrderSettingService;
    @Qualifier("jmsTemplateDelay")
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Environment environment;
    public NursingOrder getByOrderSn(String orderSn){
        LambdaQueryWrapper<NursingOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NursingOrder::getOrderSn,orderSn)
                .eq(NursingOrder::getDelFlag,"0");
        return super.getOne(queryWrapper);
    }
    /**
     * 自动关闭超时未支付订单
     * @param orderId
     */
    @Transactional
    public void closeOrderIfUnpaid(Integer orderId){
        this.baseMapper.updateStatusByIdAndStatus(orderId,0,4);
    }
    @Transactional
    public boolean cancelOrder(Integer orderId){
        LambdaUpdateWrapper<NursingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(NursingOrder::getStatus,4)
                .eq(NursingOrder::getId,orderId)
                .eq(NursingOrder::getDelFlag,"0")
                .in(NursingOrder::getStatus,0,1);
        return super.update(updateWrapper);
    }
    /** 支付完成更新状态为已支付 */
    @Transactional
    @Override
    public void updateOrderStatusToPaidByOrderNo(String orderNo){
        if(orderNo != null && orderNo.startsWith(PensionBusiness.PAYMENT_ORDER_PREFIX))
            this.baseMapper.updateStatusByOrderSn(1,orderNo);
    }
    @Transactional
    @Override
    public void updateOrderRefundAmountByOrderNo(String outTradeNo, BigDecimal refundAmount) {
        if(outTradeNo == null || !outTradeNo.startsWith(PensionBusiness.PAYMENT_ORDER_PREFIX) || refundAmount == null)
            return;
        NursingOrder order = this.baseMapper.getOneByOrderSn(outTradeNo);
        this.baseMapper.updateOrderRefundAmountByOrderNo(outTradeNo,
                order.getRefundAmount() == null ? refundAmount : refundAmount.add(order.getRefundAmount()));
    }
    @Transactional
    public boolean updateDelivery(NursingOrder order){
        //如果已经有人接单,则替换并更新被替换护工状态
        NursingOrder selectOrder = super.getById(order.getId());
        if(selectOrder == null) throw new RuntimeException("订单不存在");
        if(selectOrder.getStatus() != 1) throw new RuntimeException("订单当前状态无法进行该操作");
        if(selectOrder.getWorkerId() != null)
            nursingWorkerService.updateStatusByIdAndStatus(2,0,selectOrder.getWorkerId());
        //先更新订单
        LambdaUpdateWrapper<NursingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(NursingOrder::getWorkerId,order.getWorkerId())
                .set(NursingOrder::getStatus,2)
                .eq(NursingOrder::getId,order.getId())
                .eq(NursingOrder::getStatus,1)
                .eq(NursingOrder::getDelFlag,"0");
        //再更新护工状态
        if(nursingWorkerService.updateStatusByIdAndStatus(0,2,order.getWorkerId())
                && super.update(updateWrapper))
            return  true;
        throw new RuntimeException("失败!订单或护工有误");
    }
    @Transactional
    public boolean updateReceive(NursingOrder order){
        LambdaQueryWrapper<NursingWorker> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NursingWorker::getUserId,order.getUserId())
                .eq(NursingWorker::getDelFlag,"0")
                .orderByDesc(List.of(NursingWorker::getUpdateTime,NursingWorker::getCreateTime))
                .last("LIMIT 1");
        NursingWorker nursingWorker = nursingWorkerService.getOne(queryWrapper);
        if(nursingWorker == null) throw new RuntimeException("非护工账号无法接单");
        switch (nursingWorker.getStatus()){
            case "1" : throw new RuntimeException("当前护工账户已停用");
            case "2" : throw new RuntimeException("无法同时接多单");
        }
        order.setWorkerId(nursingWorker.getId());
        return this.updateDelivery(order);
    }
    @Transactional
    public boolean updateComplete(NursingOrder order){
        //先更新订单状态
        LambdaUpdateWrapper<NursingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(NursingOrder::getStatus,3)
                .eq(NursingOrder::getId,order.getId())
                .eq(NursingOrder::getStatus,2)
                .eq(NursingOrder::getDelFlag,"0");

        super.update(updateWrapper);
        //再更新护工状态
        nursingWorkerService.updateStatusByIdAndStatus(2,0,order.getWorkerId());
        //消息队列: 已完成订单超时未评价自动5星好评
        sendMessageAutoComment(order);
        return true;
    }
    @Transactional
    public boolean updateRecordId(NursingOrder order){
        //先更新订单状态
        return super.update(new LambdaUpdateWrapper<NursingOrder>()
                .set(NursingOrder::getRecordId,order.getRecordId())
                .eq(NursingOrder::getId,order.getId())
                .eq(NursingOrder::getDelFlag,"0"));
    }

    /**
     * 已完成订单评价
     * @param orderId
     */
    @Transactional
    public void commentOrderIfFinished(Integer orderId,String comment,Integer star){
        this.baseMapper.commentFinishedOrder(orderId,comment,star);
    }
    public void autoCommentOrderIfComplete(Integer orderId){
        commentOrderIfFinished(orderId,"超时自动5星好评",5);
    }

    @PensionDataScope(deptAlias = "a",userAlias = "a")
    public List<NursingOrder> getListByExample(NursingOrder nursingOrder){
        if(nursingOrder.getWorkerIds() != null && nursingOrder.getWorkerIds().isEmpty())
            nursingOrder.setWorkerIds(null);
        if(nursingOrder.getStatusList() != null && nursingOrder.getStatusList().isEmpty())
            nursingOrder.setStatusList(null);
        return this.baseMapper.getListByExample(nursingOrder);
    }
    @Transactional
    public boolean saveCascade(NursingOrder nursingOrder){
        //先保存主表: 订单
        if(!super.save(nursingOrder)) throw new RuntimeException("下单失败!");
        //再保存从表: 服务项目价格表
        List<NursingOrderItems> items = nursingOrder.getNursingOrderItems();
        if(items != null && !items.isEmpty())
            items.forEach(e -> e.setOrderId(nursingOrder.getId()));
        if(!nursingOrderItemsService.saveBatch(items)) throw new RuntimeException("下单失败!");
        //消息队列: 放入延迟消息处理订单超时自动关闭
        sendMessageAutoClose(nursingOrder);
        return true;
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        //进行中订单先恢复护工状态
        List<Integer> workerIds = super.listByIds((Collection<? extends Serializable>) list).stream()
                .filter(e -> e.getStatus() == 2)
                .map(NursingOrder::getWorkerId)
                .toList();
        nursingWorkerService.updateStatusByIdsAndStatus(2,0,workerIds);
        return super.removeByIds(list);
    }

    @Transactional
    public boolean updateRecordIdToNull(Collection<Integer> ids){
        return ids != null && !ids.isEmpty() && this.baseMapper.updateRecordIdToNull(ids);
    }

    private void sendMessageAutoClose(NursingOrder order){
        //获取延迟时间
        NursingOrderSetting nursingOrderSetting = nursingOrderSettingService.getOneOrAncestorByDeptId(order.getDeptId());

        //发送自动关闭延迟消息
        jmsTemplate.convertAndSend(Objects.requireNonNull(environment.getProperty("message-queue.nursing-order-close")),order.getId(), message -> {
            message.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), Instant.now().plus(nursingOrderSetting.getCloseOvertime(), ChronoUnit.MINUTES).toEpochMilli());
            return message;
        });
    }
    private void sendMessageAutoComment(NursingOrder order){
        //获取延迟时间
        NursingOrderSetting nursingOrderSetting = nursingOrderSettingService.getOneOrAncestorByDeptId(order.getDeptId());
        //发送自动完成延迟消息
        jmsTemplate.convertAndSend(Objects.requireNonNull(environment.getProperty("message-queue.nursing-order-complete")), order.getId(), message -> {
            message.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), Instant.now().plus(nursingOrderSetting.getCommentOvertime(), ChronoUnit.DAYS).toEpochMilli());
            return message;
        });
    }
}
