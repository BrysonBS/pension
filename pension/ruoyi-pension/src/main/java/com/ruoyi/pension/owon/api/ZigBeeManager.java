package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.enums.Command;
import com.ruoyi.pension.owon.domain.enums.Operation;
import com.ruoyi.pension.owon.domain.enums.TypeEnum;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class ZigBeeManager {
    @Autowired
    private AccessToken accessToken;
    @Autowired
    private OwonHttp owonHttp;

    /** 设备加入网络控制 */
    private AjaxResult permitJoin(String mac,int second,String switchgear) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回

        Argument<?,?> argument = Argument.builder()
                .switchgear(switchgear)
                .second(second)
                .build();

        Datapacket<?,?> datapacket = Datapacket.builder()
                .type(TypeEnum.ZIGBEE_CONFIG.getValue())
                .command(Command.PERMIT_JOIN.getValue())
                .session("")
                .sequence(Operation.REQUEST_PERMIT_JOIN.getCode())
                .argument(argument)
                .build();
        return owonHttp.post(mac,ajaxResult,datapacket,Operation.REQUEST_PERMIT_JOIN);
    }
    public AjaxResult permitJoinOn(String mac,int second) throws ExecutionException, InterruptedException, JsonProcessingException {
        return permitJoin(mac,second,"on");
    }
    public AjaxResult permitJoinOff(String mac,int second) throws ExecutionException, InterruptedException, JsonProcessingException {
        return permitJoin(mac,second,"off");
    }

    /**
     * 移除在网设备
     * @param mac 设备所在网关
     * @param ieee 设备ieee
     * @param ep 设备ep
     */
    public AjaxResult delete(String mac,String ieee,int ep) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回

        Argument<?,?> argument = Argument.builder()
                .ieee(ieee)
                .ep(ep)
                .build();
        Datapacket<?,?> datapacket = Datapacket.builder()
                .type(TypeEnum.ZIGBEE_CONFIG.getValue())
                .command(Command.DELETE.getValue())
                .session("")
                .sequence(Operation.REQUEST_DELETE_ZB.getCode())
                .argument(argument)
                .build();
        return owonHttp.post(mac,ajaxResult,datapacket,Operation.REQUEST_DELETE_ZB);
    }

    /**
     * 设置设备节点名称
     * @param mac 网关mac地址
     * @param ieee 设备ieee
     * @param ep 设备ep
     * @param name 要设置的名称
     * @return
     */
    public AjaxResult rename(String mac,String ieee,int ep,String name) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回

        Argument<?,?> argument = Argument.builder()
                .ieee(ieee)
                .ep(ep)
                .name(name)
                .build();
        Datapacket<?,?> datapacket = Datapacket.builder()
                .type(TypeEnum.ZIGBEE_CONFIG.getValue())
                .command(Command.RENAME.getValue())
                .session("")
                .sequence(Operation.REQUEST_RENAME_ZB.getCode())
                .argument(argument)
                .build();
        return owonHttp.post(mac,ajaxResult,datapacket,Operation.REQUEST_RENAME_ZB);
    }

    /**
     * 获取设备的当前状态
     * @param mac 网关地址
     * @param array 要查询的网关下设备
     * @return
     */
    public AjaxResult multiDev(String mac, DeviceEp[] array) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回

        Operation operation = Operation.REQUEST_STATE_ZB;
        //统一会话序列号
        for(DeviceEp ep : array)
            ep.setSequence(operation.getCode());

        Argument<?,?> argument = Argument.builder()
                .deviceList(array)
                .build();
        Datapacket<?,?> datapacket = Datapacket.builder()
                .type(TypeEnum.STATE.getValue())
                .command(Command.MULTIDEV.getValue())
                .session("")
                .sequence(operation.getCode())
                .argument(argument)
                .build();

        ArrayList<String> keys = new ArrayList<>();
        for(DeviceEp ep : array)
            keys.add(ep.getIeee());
        return owonHttp.postOfCache(mac,ajaxResult,datapacket,Operation.RESULT_STATE_ZB,keys);
    }
}
