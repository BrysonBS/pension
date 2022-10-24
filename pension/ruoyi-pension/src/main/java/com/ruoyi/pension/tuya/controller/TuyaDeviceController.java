package com.ruoyi.pension.tuya.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.tuya.domain.TuyaDevice;
import com.ruoyi.pension.tuya.service.TuyaDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/tuya/device")
public class TuyaDeviceController extends BaseController {
    @Autowired
    private TuyaDeviceService tuyaDeviceService;
    @GetMapping("/list")
    public TableDataInfo listByExample(TuyaDevice tuyaDevice){
        startPage();
        List<TuyaDevice> list = tuyaDeviceService.listByExample(tuyaDevice);
        return getDataTable(list);
    }
    @GetMapping("/byId/{id}")
    public AjaxResult getById(@PathVariable("id") String id){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,tuyaDeviceService.getById(id));
    }
    @PreAuthorize("@ss.hasPermi('tuya:device:edit')")
    @Log(title = "音视频管理/摄像头", businessType = BusinessType.UPDATE)
    @PatchMapping
    public AjaxResult update(@RequestBody TuyaDevice tuyaDevice){
        if(tuyaDevice.getId() == null)
            return AjaxResult.error("不存在,无法更新");
        return toAjax(tuyaDeviceService.updateUserIdAndDeptIdById(tuyaDevice));
    }
    @PreAuthorize("@ss.hasPermi('tuya:device:remove')")
    @Log(title = "音视频管理/摄像头", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult delete(@RequestParam("id") Collection<Long> ids){
        return toAjax(tuyaDeviceService.removeBatchByIds(ids));
    }
    @PreAuthorize("@ss.hasPermi('tuya:device:refresh')")
    @GetMapping("/refresh")
    public AjaxResult refresh(){
        return toAjax(tuyaDeviceService.refresh());
    }
}
