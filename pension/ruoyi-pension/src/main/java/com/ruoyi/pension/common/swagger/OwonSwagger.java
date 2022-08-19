package com.ruoyi.pension.common.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.api.*;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.dto.OwonReport;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.common.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import com.ruoyi.pension.owon.domain.result.OwonResult;
import com.ruoyi.pension.owon.service.OwonReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "欧万相关接口API")
@RequestMapping("/owon")
public class OwonSwagger {
    @Autowired
    private OwonReportService owonReportService;
    @io.swagger.v3.oas.annotations.Operation(summary = "欧万上报",security = { @SecurityRequirement(name = "Authorization") },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = """         
                                    {
                                       "code": "102",
                                       "mac": "3C6A2CFFFED0FF3D",
                                       "ts": "2022-05-06 11:55:21",
                                       "sjson": "{\\"argument\\":{\\"ieee\\":\\"78BA2B7C95E9\\",\\"ep\\":1,\\"status\\":5,\\"devType\\":48642,\\"heartRate\\":65,\\"respiratoryRate\\":15,\\"sleepFlag\\":0,\\"statusValue\\":0,\\"dataType\\":0,\\"awakeFlag\\":1},\\"type\\":\\"update\\",\\"command\\":\\"bleDataReport\\"}",
                                       "token": "kIt-79JOk5"
                                    }   
                                    """
                    ))
            ))
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

    @io.swagger.v3.oas.annotations.Operation(summary = "获取AccessToken",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/getAccessToken")
    public AjaxResult getAccessToken() throws ExecutionException, InterruptedException, JsonProcessingException {
        return accessToken.getAccessToken();
    }
    @io.swagger.v3.oas.annotations.Operation(summary = "刷新AccessToken",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/refreshAccessToken")
    public AjaxResult refreshAccessToken() throws ExecutionException, JsonProcessingException, InterruptedException {
        return accessToken.refreshToken();
    }
    @io.swagger.v3.oas.annotations.Operation(summary = "获取设备EP节点列表",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/getEpList")
    public AjaxResult getEpList() throws ExecutionException, InterruptedException, JsonProcessingException {
        return deviceList.getEpListByMac("3C6A2CFFFED0FF3D");
    }
    @io.swagger.v3.oas.annotations.Operation(summary = "获取设备EP当前状态",security = { @SecurityRequirement(name = "Authorization") })
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
    @io.swagger.v3.oas.annotations.Operation(summary = "获取报警器传感器状态",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/getWarning")
    public AjaxResult getWarning(String ieee,Integer ep) throws ExecutionException, InterruptedException, JsonProcessingException {
        String mac = "3C6A2CFFFED0FF3D";
        DeviceEp deviceEp = DeviceEp.builder()
                .ieee(ieee)
                .ep(ep)
                .build();
        return sirenSensorManager.getWarning(mac,deviceEp);
    }
    @io.swagger.v3.oas.annotations.Operation(summary = "开启/关闭报警器",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/startWarning")
    @Parameters({
            @Parameter(name = "ieee", description = "设备ieee", schema = @Schema(name = "String",implementation = String.class)),
            @Parameter(name = "ep", description = "设备ep", schema=@Schema(name = "Integer",implementation = Integer.class)),
            @Parameter(name = "option", description = "设备报警的模式", schema=@Schema(name = "Integer",implementation = Integer.class)),
            @Parameter(name = "duration", description = "报警持续时间(秒)", schema=@Schema(name = "Integer",implementation = Integer.class))
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


    @io.swagger.v3.oas.annotations.Operation(summary = "BLE设备加网",security = { @SecurityRequirement(name = "Authorization") })
    @Parameters({
            @Parameter(name = "mac", description = "要加入的网关mac", schema = @Schema(name = "String",implementation = String.class)),
            @Parameter(name = "serialNo", description = "设备厂商序列号", schema = @Schema(name = "String",implementation = String.class))
    })
    @GetMapping("/scanBleDevice")
    public AjaxResult scanBleDevice(String mac,String serialNo,Integer ep) throws ExecutionException, InterruptedException, JsonProcessingException {
        return bleManager.scanBleDevice(mac,serialNo,ep);
    }
}
