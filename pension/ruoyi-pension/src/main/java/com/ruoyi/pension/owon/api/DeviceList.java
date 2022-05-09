package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.owon.config.OwonProps;
import com.ruoyi.pension.owon.convertor.OwonTsConvertor;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.dto.OwonRequest;
import com.ruoyi.pension.owon.domain.dto.Response;
import com.ruoyi.pension.owon.domain.enums.Command;
import com.ruoyi.pension.owon.domain.enums.Operation;
import com.ruoyi.pension.owon.domain.enums.TypeEnum;
import com.ruoyi.pension.owon.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.util.concurrent.*;

@Component
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
    public AjaxResult getEpListByMac(String mac) throws ExecutionException, InterruptedException, JsonProcessingException {
        AjaxResult ajaxResult = accessToken.getAccessToken();
        if((int)(ajaxResult.get(AjaxResult.CODE_TAG)) != HttpStatus.OK.value())
            return ajaxResult; //获取失败直接返回
/*        //先尝试去redis缓存中取
        Response<?> responseCache = redisCache.getCacheObject(Operation.RESULT_EP_LIST.getCode()+mac);;
        if(responseCache != null){
            return AjaxResult.success()
                    .put(AjaxResult.CODE_TAG,HttpStatus.OK.value())
                    .put(AjaxResult.DATA_TAG,objectMapper.writeValueAsString(responseCache));
        }*/
        //没取到再查询
        Datapacket<?,?> datapacket = Datapacket
                .builder()
                .type(TypeEnum.ZIGBEE_CONFIG.getValue())
                .command(Command.EP_LIST.getValue())
                .session("")
                .sequence(Operation.REQUEST_EP_LIST.getCode())
                .build();
        //发送请求并返回
        return owonHttp.postOfCache(mac,ajaxResult,datapacket,Operation.RESULT_EP_LIST,mac);
    }
}
