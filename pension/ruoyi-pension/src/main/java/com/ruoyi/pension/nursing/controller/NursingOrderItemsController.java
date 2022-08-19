package com.ruoyi.pension.nursing.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.nursing.domain.po.NursingOrderItems;
import com.ruoyi.pension.nursing.service.NursingOrderItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nursing/order/items")
public class NursingOrderItemsController {
    @Autowired
    private NursingOrderItemsService nursingOrderItemsService;

    @GetMapping("/listByOrderId")
    public AjaxResult getListByOrderId(Integer orderId){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingOrderItemsService.getListByOrderId(orderId));
    }
}
