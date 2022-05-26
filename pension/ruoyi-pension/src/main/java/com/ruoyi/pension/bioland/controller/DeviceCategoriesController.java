package com.ruoyi.pension.bioland.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.bioland.service.DeviceCategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("BiolandDeviceCategoriesController")
@RequestMapping("/device/bioland/categories")
public class DeviceCategoriesController {
    @Autowired
    @Qualifier("BiolandDeviceCategoriesService")
    private DeviceCategoriesService deviceCategoriesService;
    @GetMapping("/typeList")
    public AjaxResult list(){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG, deviceCategoriesService.getListTryCache());
    }
}
