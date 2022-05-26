package com.ruoyi.pension.owon.controller.health;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.domain.po.VBleDataReport;
import com.ruoyi.pension.owon.service.VBleDataReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/health")
public class SleepController extends BaseController{
    @Autowired
    private VBleDataReportService bleDataReportService;
    @GetMapping("/sleep")
    public AjaxResult sleepList(String ieee, LocalDateTime beginTime, LocalDateTime endTime){
        List<VBleDataReport> list;
        if(ieee == null) list = List.of();
        else list = bleDataReportService.listLatest(ieee,beginTime,endTime);
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,list);
    }
}
