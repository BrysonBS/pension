package com.ruoyi.pension.owon.controller.device;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.pension.owon.domain.po.OwonNotice;
import com.ruoyi.pension.owon.service.OwonNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/device/notice")
public class DeviceNoticeController extends BaseController {
    @Autowired
    private OwonNoticeService owonNoticeService;
    @GetMapping("/list")
    public TableDataInfo list(LocalDateTime beginTime, LocalDateTime endTime, OwonNotice notice){
        if(endTime != null) endTime = endTime.plusDays(1);
        startPage();
        List<OwonNotice> list = owonNoticeService.getList(
                getLoginUser().getUser().isAdmin(),
                getLoginUser().getDeptId(), beginTime, endTime,
                notice
        );
        return getDataTable(list);
    }

    @DeleteMapping("/batch")
    public AjaxResult deleteNotice(Integer[] id){
        return toAjax(owonNoticeService.removeByIds(List.of(id)));
    }
}
