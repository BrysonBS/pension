package com.ruoyi.pension.nursing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.nursing.domain.po.NursingOrderSetting;
import com.ruoyi.pension.nursing.service.NursingOrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nursing/order/setting")
public class NursingOrderSettingController extends BaseController {
    @Autowired
    private NursingOrderSettingService nursingOrderSettingService;

    @GetMapping
    public AjaxResult get(){
        return AjaxResult.success()
                        .put(AjaxResult.DATA_TAG,
                                nursingOrderSettingService.getOneOrAncestorByDeptId(getDeptId()));
    }

    @PatchMapping
    public AjaxResult saveOrUpdate(@RequestBody NursingOrderSetting nursingOrderSetting){
        NursingOrderSettingController orderSettingController = SpringUtils.getBean(this.getClass());
        //新增是添加归属部门
        if(nursingOrderSetting.getId() == null){
            nursingOrderSetting.setDeptId(getDeptId());
            return orderSettingController.add(nursingOrderSetting);
        }
        return orderSettingController.update(nursingOrderSetting);
    }

    @PreAuthorize("@ss.hasPermi('nursing:order:setting:edit')")
    @OperationInfo(OperationInfo.Info.CREATED)//添加创建人和创建时间
    @Log(title = "护理管理/订单中心/订单设置", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult add(@RequestBody NursingOrderSetting nursingOrderSetting){
        return toAjax(nursingOrderSettingService.save(nursingOrderSetting));
    }

    @PreAuthorize("@ss.hasPermi('nursing:order:setting:edit')")
    @OperationInfo(OperationInfo.Info.UPDATED)//添加创建人和创建时间
    @Log(title = "护理管理/订单中心/订单设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody NursingOrderSetting nursingOrderSetting){
        return toAjax(nursingOrderSettingService.updateById(nursingOrderSetting));
    }

}
