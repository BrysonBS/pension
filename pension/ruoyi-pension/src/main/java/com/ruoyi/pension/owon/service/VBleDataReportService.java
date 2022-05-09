package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.owon.domain.po.VBleDataReport;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.VBleDataReportMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author Administrator
* @description 针对表【v_ble_data_report】的数据库操作Service
* @createDate 2022-05-05 14:44:47
*/
@Service
public class VBleDataReportService extends ServiceImpl<VBleDataReportMapper, VBleDataReport> implements IService<VBleDataReport> {
    public List<VBleDataReport> listLatest(String ieee, LocalDateTime beginTime, LocalDateTime endTime){
        if(endTime != null) endTime = endTime.plusDays(1);
        LambdaQueryWrapper<VBleDataReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ieee != null,VBleDataReport::getIeee,ieee)
                .ge(beginTime != null,VBleDataReport::getTs,beginTime)
                .lt(endTime != null,VBleDataReport::getTs,endTime)
                .orderByDesc(VBleDataReport::getId);
                //.last("limit 20");
        return list(queryWrapper);
    }
}
