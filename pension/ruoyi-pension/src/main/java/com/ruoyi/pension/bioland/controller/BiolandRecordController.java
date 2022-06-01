package com.ruoyi.pension.bioland.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.bioland.domain.po.BiolandDevice;
import com.ruoyi.pension.bioland.domain.po.SrcDataValue;
import com.ruoyi.pension.bioland.service.SrcDataValueService;
import com.ruoyi.pension.owon.domain.po.VBleDataReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bioland/record")
public class BiolandRecordController {
    @Autowired
    private SrcDataValueService srcDataValueService;
    @GetMapping("/data")
    public AjaxResult blpressureList(String ieee, LocalDateTime beginTime, LocalDateTime endTime){
        List<SrcDataValue> list;
        if(ieee == null) list = List.of();
        else list = srcDataValueService.listLatest(ieee,beginTime,endTime);
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,list);
    }
}
