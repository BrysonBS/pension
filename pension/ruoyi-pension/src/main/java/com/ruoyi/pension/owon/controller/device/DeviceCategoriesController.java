package com.ruoyi.pension.owon.controller.device;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.owon.domain.po.DeviceCategories;
import com.ruoyi.pension.owon.service.DeviceCategoriesService;
import com.ruoyi.pension.owon.service.DeviceService;
import com.ruoyi.pension.owon.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/device/categories")
public class DeviceCategoriesController extends BaseController {
    @Autowired
    private DeviceCategoriesService deviceCategoriesService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private RedisCache redisCache;
    /**
     * 设备类型列表
     * @return
     */
    @GetMapping("/list")
    public AjaxResult getList(){
        //先尝试从缓存获取
        List<DeviceCategories> list = redisCache.getCacheObject("dict:" + DeviceCategories.class.getName());
        if(list == null) { //没找到缓存就查找
            list = deviceCategoriesService.getIdAndNameList();
            redisCache.setCacheObject("dict:" + DeviceCategories.class.getName(), list, 60, TimeUnit.MINUTES);
        }
        return AjaxResult.success()
        .put(AjaxResult.DATA_TAG,list.toArray(DeviceCategories[]::new));
    }
    @GetMapping("/deviceList")
    /** 获取所属部门的设备列表 */
    public AjaxResult getDeviceList(){
        Long deptId = getLoginUser().getDeptId();
        return deptId == null ? AjaxResult.error("未登录") : AjaxResult.success()
                .put(AjaxResult.DATA_TAG,deviceService.getSleepListByDeptId(deptId));
    }
    /**
     * 获取网关列表
     * @return
     */
    @GetMapping("/gwList")
    public AjaxResult getGWList() {
        if (getLoginUser().getUser().isAdmin())
            return AjaxResult.success()
                    .put(AjaxResult.DATA_TAG, gatewayService.list());
        else return AjaxResult.success()
                    .put(AjaxResult.DATA_TAG, Collections.EMPTY_LIST);
    }
}
