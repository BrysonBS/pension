package com.ruoyi.pension.owon.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.api.*;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.dto.OwonReport;
import com.ruoyi.pension.owon.domain.enums.Operation;
import com.ruoyi.pension.owon.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import com.ruoyi.pension.owon.domain.result.OwonResult;
import com.ruoyi.pension.owon.service.OwonReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@Api("欧万相关接口API")
@RequestMapping("/owonTest")
public class OwonSwagger {
    @Autowired
    private OwonReportService owonReportService;
    @ApiOperation("欧万上报")
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
        if(!Strings.isEmpty(dataPacket.getSjson())) { //未记录格式,反序列化失败
            log.error(System.getProperty("line.separator")+"test:error:report:  "+resultLog);
            return OwonResult.failure();
        }
        else log.info(System.getProperty("line.separator")+"test:success:report:  "+resultLog);

        dataPacket.setSource(Platform.OWON);
        dataPacket.setTarget(Platform.NATIVE);

        if(!(dataPacket.getOperation() == Operation.REPORT_HEART_BEAT)) //心跳同步数据不保存
            owonReportService.saveCascade(report);

        // 测试接口只打印日志不保存
        return OwonResult.success();
    }

    @Autowired
    private AccessToken accessToken;
    @Autowired
    private DeviceList deviceList;
    @Autowired
    private ZigBeeManager zigBeeManager;
    @Autowired
    private SirenSensorManager sirenSensorManager;
    @Autowired
    private BleManager bleManager;

    @ApiOperation("获取AccessToken")
    @GetMapping("/getAccessToken")
    public AjaxResult getAccessToken() throws ExecutionException, InterruptedException, JsonProcessingException {
        return accessToken.getAccessToken();
    }
    @ApiOperation("获取设备EP节点列表")
    @GetMapping("/getEpList")
    public AjaxResult getEpList() throws ExecutionException, InterruptedException, JsonProcessingException {
        return deviceList.getEpListByMac("3C6A2CFFFED0FF3D");
    }
    @ApiOperation("获取设备EP当前状态")
    @GetMapping("/getEpState")
    public AjaxResult getEpState(String[] ieees) throws ExecutionException, InterruptedException, JsonProcessingException {
        DeviceEp[] array = new DeviceEp[ieees.length];
        for(int i = 0; i < array.length; i++){
            array[i] = DeviceEp.builder()
                    .devtype(1026)
                    .cache(1)
                    .ieee(ieees[i])
                    .ep(1)
                    .build();
        }
        String mac = "3C6A2CFFFED0FF3D";
        return zigBeeManager.multiDev(mac, array);
    }
    @ApiOperation("获取报警器传感器状态")
    @GetMapping("/getWarning")
    public AjaxResult getWarning(String ieee,Integer ep) throws ExecutionException, InterruptedException, JsonProcessingException {
        String mac = "3C6A2CFFFED0FF3D";
        DeviceEp deviceEp = DeviceEp.builder()
                .ieee(ieee)
                .ep(ep)
                .build();
        return sirenSensorManager.getWarning(mac,deviceEp);
    }
    @ApiOperation("开启/关闭报警器")
    @GetMapping("/startWarning")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ieee", value = "设备ieee", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "ep", value = "设备ep", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "option", value = "设备报警的模式", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "duration", value = "报警持续时间(秒)", dataType = "Integer", dataTypeClass = Integer.class)
    })
    public AjaxResult startWarning(DeviceEp deviceEp) throws ExecutionException, InterruptedException, JsonProcessingException {
        String mac = "3C6A2CFFFED0FF3D";
        DeviceEp ep = DeviceEp.builder()
                .ieee(deviceEp.getIeee())
                .ep(deviceEp.getEp())
                .option(deviceEp.getOption())
                .duration(deviceEp.getDuration())
                .build();
        return sirenSensorManager.getWarning(mac,ep);
    }


    @ApiOperation("BLE设备加网")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mac", value = "要加入的网关mac", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "serialNo", value = "设备厂商序列号", dataType = "String", dataTypeClass = String.class)
    })
    @GetMapping("/scanBleDevice")
    public AjaxResult scanBleDevice(String mac,String serialNo) throws ExecutionException, InterruptedException, JsonProcessingException {
        return bleManager.scanBleDevice(mac,serialNo);
    }
}
