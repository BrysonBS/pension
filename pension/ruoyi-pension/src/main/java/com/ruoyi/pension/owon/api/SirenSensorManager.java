package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.common.domain.enums.Command;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.common.domain.enums.TypeEnum;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class SirenSensorManager {
    @Autowired
    private AccessToken accessToken;
    @Autowired
    private OwonHttp owonHttp;

    /**
     * 报警器传感器获取状态
     * @param mac 网关mac地址
     * @param ep 报警器设备
     */
    public AjaxResult getWarning(String mac, DeviceEp ep) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回

        Argument<?,?> argument = Argument.builder()
                .ieee(ep.getIeee())
                .ep(ep.getEp())
                .build();
        Datapacket<?,?> datapacket = Datapacket.builder()
                .session("")
                .type(TypeEnum.MONITOR.getValue())
                .command(Command.GET_WARNING.getValue())
                .sequence(Operation.REQUEST_GET_WARNING.getCode())
                .argument(argument)
                .build();
        return owonHttp.postOfCache(mac,ajaxResult,datapacket,Operation.RESULT_GET_WARNING,ep.getIeee());
    }

    /**
     *开启/ 关闭报警器
     * @param mac
     * @param ep
     */
    public AjaxResult startWarning(String mac,DeviceEp ep) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回

        Argument<?,?> argument = Argument.builder()
                .ieee(ep.getIeee())
                .ep(ep.getEp())
                .option(ep.getOption())
                .duration(ep.getDuration())
                .build();
        Datapacket<?,?> datapacket = Datapacket.builder()
                .session("")
                .type(TypeEnum.MONITOR.getValue())
                .command(Command.START_WARNING.getValue())
                .sequence(Operation.REQUEST_START_WARNING.getCode())
                .argument(argument)
                .build();
        return owonHttp.post(mac,ajaxResult,datapacket,Operation.REQUEST_START_WARNING);
    }
}
