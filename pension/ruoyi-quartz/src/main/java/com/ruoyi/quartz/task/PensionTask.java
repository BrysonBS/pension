package com.ruoyi.quartz.task;

import com.ruoyi.pension.common.service.LogSmsService;
import com.ruoyi.pension.owon.service.OwonReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component("pensionTask")
public class PensionTask {
    @Autowired
    private OwonReportService owonReportService;
    @Autowired
    private LogSmsService logSmsService;

    /**
     * 欧万设备上报数据清理
     * @param endDate
     */
    public void deletePeriod(String endDate){
        //日期格式
        String reg = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";
        if(endDate != null && endDate.matches(reg)){
            LocalDate localDate = LocalDate.parse(endDate,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if(localDate.plusMonths(1).isAfter(LocalDate.now()))
                localDate = LocalDate.now().minusMonths(1);
            endDate = localDate
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            owonReportService.deleteLtCreatedCascade(endDate);
            return;
        }
        deletePeriod();
    }
    public void deletePeriod(){
        String endDate = LocalDate.now()
                .minusMonths(6)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        owonReportService.deleteLtCreatedCascade(endDate);
    }


    /**
     * 短信日志清理
     * @param endDate
     */
    public void deleteLogSms(String endDate){
        //日期格式
        String reg = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";
        if(endDate != null && endDate.matches(reg)){
            LocalDate localDate = LocalDate.parse(endDate,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if(localDate.plusDays(15).isAfter(LocalDate.now()))
                localDate = LocalDate.now().minusMonths(1);
            logSmsService.deleteBySendTimeLessThan(localDate.atStartOfDay());
            return;
        }
        deleteLogSms();
    }
    public void deleteLogSms(){
        LocalDateTime localDateTime = LocalDate.now()
                .minusMonths(6)
                .atStartOfDay();
        logSmsService.deleteBySendTimeLessThan(localDateTime);
    }
}
