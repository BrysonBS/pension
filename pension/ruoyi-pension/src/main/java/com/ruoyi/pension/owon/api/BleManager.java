package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.common.domain.enums.Command;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.common.domain.enums.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class BleManager {
    @Autowired
    private AccessToken accessToken;
    @Autowired
    private OwonHttp owonHttp;

    /**
     * 获取BLE设备列表
     * @param mac
     * @return
     */
    public AjaxResult getBleListByMac(String mac) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回
        //没取到再查询
        Datapacket<?,?> datapacket = Datapacket
                .builder()
                .type(TypeEnum.BLE_MODULE.getValue())
                .command(Command.GET_BLE_LIST.getValue())
                .session("")
                .sequence(Operation.REQUEST_BLE_LIST.getCode())
                .build();
        //发送请求并返回
        return owonHttp.postOfCache(mac,ajaxResult,datapacket,Operation.RESULT_BLE_LIST,mac);
    }

    /**
     * ble设备加网
     * @return
     */
    public AjaxResult scanBleDevice(String mac,String serialNo) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回
        //没取到再查询
        Argument<?,?> argument = Argument
                .builder()
                .ep(1)
                .connectType(1)
                .serialNo(serialNo)
                .build();

        Datapacket<?,?> datapacket = Datapacket
                .builder()
                .type(TypeEnum.BLE_MODULE.getValue())
                .command(Command.SCAN_BLE_DEVICE.getValue())
                .session("")
                .sequence(Operation.REQUEST_BLE_SCAN.getCode())
                .argument(argument)
                .build();
        //发送请求并返回
        return owonHttp.post(mac,ajaxResult,datapacket,Operation.RESULT_BLE_SCAN);
    }
}
