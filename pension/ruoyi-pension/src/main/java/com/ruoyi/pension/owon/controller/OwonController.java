package com.ruoyi.pension.owon.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.pension.owon.domain.enums.Operation;
import com.ruoyi.pension.owon.domain.enums.Platform;
import com.ruoyi.pension.owon.service.OwonReportService;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.dto.OwonReport;
import com.ruoyi.pension.owon.domain.result.OwonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;


@Slf4j
@RestController
public class OwonController {
    @Autowired
    private OwonReportService owonReportService;
    @PostMapping(value="/owonReport", produces = "application/json;charset=UTF-8")
    public OwonResult getOwonReportData(@RequestBody @Valid OwonReport report) throws Exception {
        //添加接口记录
        report.setCreated(LocalDateTime.now());
        Datapacket<?,?> dataPacket = report.getSjson();
        String resultLog = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .build()
                .writeValueAsString(report);
        if(dataPacket == null || !Strings.isEmpty(dataPacket.getSjson())) { //未记录格式,反序列化失败
            log.error(System.getProperty("line.separator")+"error:report:  "+resultLog);
            return OwonResult.failure();
        }

        dataPacket.setSource(Platform.OWON);
        dataPacket.setTarget(Platform.NATIVE);

        //if(!(dataPacket.getOperation() == Operation.REPORT_HEART_BEAT)) //心跳同步数据不保存
        owonReportService.saveCascade(report);
        return OwonResult.success();
    }
}
