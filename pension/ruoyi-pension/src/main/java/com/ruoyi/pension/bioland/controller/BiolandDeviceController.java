package com.ruoyi.pension.bioland.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.bioland.domain.po.BiolandDevice;
import com.ruoyi.pension.bioland.service.BiolandDeviceService;
import com.ruoyi.pension.common.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.DevicePhone;
import com.ruoyi.pension.owon.service.DevicePhoneService;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device/bioland")
public class BiolandDeviceController extends BaseController {
    @Autowired
    private BiolandDeviceService biolandDeviceService;
    @Autowired
    private DevicePhoneService devicePhoneService;
    //获取列表
    @GetMapping("/list")
    public TableDataInfo getListByExample(BiolandDevice device){
        startPage();
        List<BiolandDevice> list = biolandDeviceService.getListByExample(device);
        return getDataTable(list);
    }
    //下拉选择列表
    @GetMapping("/blpressureList")
    public AjaxResult getBlpressureList(BiolandDevice device){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,biolandDeviceService.getListByExample(device));
    }
    //根据id获取设备和绑定手机号
    @GetMapping("/{id}")
    public AjaxResult getDevice(@PathVariable("id") Integer id){
        BiolandDevice biolandDevice = biolandDeviceService.getById(id);
        if(biolandDevice == null) return AjaxResult.error();
        List<DevicePhone> phones = devicePhoneService.getByDeviceIdAndSource(id, Platform.BIOLAND);
        biolandDevice.setPhones(phones.toArray(DevicePhone[]::new));
        biolandDevice.setPhonesId(new Integer[]{});
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,biolandDevice);
    }
    /** 更新 */
    @PatchMapping
    @Log(title = "设备管理", businessType = BusinessType.UPDATE)
    public AjaxResult putBiolandDevice(@RequestBody BiolandDevice device){
        return toAjax(biolandDeviceService.updateNameAndDeptIdAndPhonesById(device));
    }
    //根据id批量删除
    @DeleteMapping("/batch")
    public AjaxResult delete(Integer[] id){
        return toAjax(biolandDeviceService.deleteDeviceAndPhoneByIds(List.of(id)));
    }
}
