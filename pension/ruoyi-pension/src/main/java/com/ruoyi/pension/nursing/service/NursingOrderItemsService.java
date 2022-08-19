package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.nursing.domain.po.NursingOrderItems;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.mapper.NursingOrderItemsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_order_items】的数据库操作Service
* @createDate 2022-08-10 11:04:07
*/
@Service
public class NursingOrderItemsService extends ServiceImpl<NursingOrderItemsMapper, NursingOrderItems> implements IService<NursingOrderItems> {

    public List<NursingOrderItems> getListByOrderId(Integer orderId){
        LambdaQueryWrapper<NursingOrderItems> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NursingOrderItems::getOrderId,orderId);
        return super.list(queryWrapper);
    }
}
