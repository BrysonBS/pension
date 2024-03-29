package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.common.domain.enums.Command;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.common.domain.enums.TypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
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
    public AjaxResult getBleListByMac(String mac){
        try {
            AjaxResult ajaxResult = accessToken.getAccessToken();
            if ((int) (ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
                return ajaxResult; //获取失败直接返回
            //没取到再查询
            Datapacket<?, ?> datapacket = Datapacket
                    .builder()
                    .type(TypeEnum.BLE_MODULE.getValue())
                    .command(Command.GET_BLE_LIST.getValue())
                    .session("")
                    .sequence(Operation.REQUEST_BLE_LIST.getCode())
                    .build();
            //发送请求并返回
            return owonHttp.postOfCache(mac, ajaxResult, datapacket, Operation.RESULT_BLE_LIST, mac);
        }catch (Exception e){
            log.error("获取BLE设备列表失败!",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * ble设备加网
     * @return
     */
    public AjaxResult scanBleDevice(String mac,String serialNo,int ep) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回
        //没取到再查询
        Argument<?,?> argument = Argument
                .builder()
                .ep(ep)
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
