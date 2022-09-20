package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.common.domain.enums.Command;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.common.domain.enums.TypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@Slf4j
public class DeviceList {
    @Autowired
    private AccessToken accessToken;
/*    @Autowired
    private RedisCache redisCache;*/
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OwonHttp owonHttp;

    /**
     * 根据网关mac地址获取EP节点列表
     * @param mac
     * @return
     */
    public AjaxResult getEpListByMac(String mac){
        try {
            AjaxResult ajaxResult = accessToken.getAccessToken();
            if ((int) (ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
                return ajaxResult; //获取失败直接返回
            //没取到再查询
            Datapacket<?, ?> datapacket = Datapacket
                    .builder()
                    .type(TypeEnum.ZIGBEE_CONFIG.getValue())
                    .command(Command.EP_LIST.getValue())
                    .session("")
                    .sequence(Operation.REQUEST_EP_LIST.getCode())
                    .build();
            //发送请求并返回
            return owonHttp.postOfCache(mac, ajaxResult, datapacket, Operation.RESULT_EP_LIST, mac);
        }catch (Exception e){
            log.error("获取EP节点列表失败!",e);
            throw new RuntimeException(e);
        }
    }
}
