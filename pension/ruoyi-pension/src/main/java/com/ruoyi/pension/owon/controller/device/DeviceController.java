package com.ruoyi.pension.owon.controller.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.owon.api.BleManager;
import com.ruoyi.pension.owon.api.DeviceList;
import com.ruoyi.pension.owon.api.ZigBeeManager;
import com.ruoyi.pension.common.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.Device;
import com.ruoyi.pension.owon.domain.po.DevicePhone;
import com.ruoyi.pension.owon.service.DevicePhoneService;
import com.ruoyi.pension.owon.service.DeviceService;
import com.ruoyi.pension.owon.service.GatewayService;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/device/device")
public class DeviceController extends BaseController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SysDeptOwonService deptOwonService;
    @Autowired
    private DeviceList deviceList;
    @Autowired
    private ZigBeeManager zigBeeManager;
    @Autowired
    private DevicePhoneService devicePhoneService;
    @Autowired
    private BleManager bleManager;
    @Autowired
    private GatewayService gatewayService;


    @GetMapping("/list")
    public TableDataInfo list(Device device) throws ExecutionException, InterruptedException, JsonProcessingException {
        //不存则默认所属部门id
        Long deptId = (deptId = device.getDeptId()) == null ?
                getLoginUser().getDeptId() : deptId;
        device.setDeptId(deptId);
        List<Long> deptIds = deptId == 100L ?
                List.of(100L)
                : deptOwonService.getListDeptAndChildrenByDeptId(deptId);

        //获取网关mac列表更新列表
        List<String> macs = gatewayService.selectAllByDeptIds(deptIds);
        //deviceService.selectAllByDeptIds(deptIds);
        for(String mac : macs){
            deviceList.getEpListByMac(mac);//EP列表
            bleManager.getBleListByMac(mac);//BLE列表
        }
        startPage();
        List<Device> list = deviceService.getListByDeptIdsAndDevice(deptIds,device);
        return getDataTable(list);
    }
    /** 根据id查询设备 */
    @GetMapping("/{id}")
    public AjaxResult getDevice(@PathVariable("id") Integer id){
        Device deviceResult = deviceService.getOneById(id);
        if(deviceResult == null) return AjaxResult.error();
        List<DevicePhone> phones = devicePhoneService.getByDeviceIdAndSource(id, Platform.OWON);
        deviceResult.setPhones(phones.toArray(DevicePhone[]::new));
        deviceResult.setPhonesId(new Integer[0]);
        return AjaxResult.success()
                    .put(AjaxResult.DATA_TAG,deviceResult);
    }
    /** 修改设备名称或归属部门 */
    @PreAuthorize("@ss.hasPermi('device:device:edit')")
    @Log(title = "设备管理", businessType = BusinessType.UPDATE)
    @PatchMapping()
    public AjaxResult setNameOrDept(@RequestBody Device device) throws ExecutionException, InterruptedException, JsonProcessingException {
        if(device.getName() != null){
            zigBeeManager.rename(device.getGwCode(),device.getIeee(),device.getEp(),device.getName());
        }
        return  toAjax(deviceService.updateDeviceAndPhoneAndCategoriesId(device));
    }

    /**
     * 根据id批量删除
     * @param id
     * @return
     */
    @DeleteMapping("/batch")
    public AjaxResult delete(Integer[] id){
        return toAjax(deviceService.removeDeviceAndPhoneByIds(List.of(id)));
    }
}
