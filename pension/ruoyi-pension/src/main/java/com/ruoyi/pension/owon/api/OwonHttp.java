package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.config.OwonProps;
import com.ruoyi.pension.owon.convertor.OwonTsConvertor;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.dto.Response;
import com.ruoyi.pension.owon.domain.enums.Operation;
import com.ruoyi.pension.owon.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class OwonHttp {
    @Autowired
    private OwonProps owonProps;
    @Autowired
    private AccessToken accessToken;
    @Autowired
    private ObjectMapper objectMapper;
    private JmsTemplate jmsTemplate;

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setReceiveTimeout(1000);
    }

    public String post(String mac, AjaxResult accessToken, Datapacket<?,?> datapacket) throws JsonProcessingException, ExecutionException, InterruptedException {
        com.ruoyi.pension.owon.domain.dto.OwonRequest request = com.ruoyi.pension.owon.domain.dto.OwonRequest.builder()
                .agentId(owonProps.getAgentId())
                .mac(mac)
                .accessToken((String) accessToken.get(AjaxResult.DATA_TAG))
                .sjson(objectMapper.writeValueAsString(datapacket))
                .ts(OwonTsConvertor.getNowTs()+"")
                .build();
        String jsonParams = objectMapper.writeValueAsString(request);
        String url = owonProps.getServer_china() + owonProps.getUri_sendGwData();
        //发送请求
        return HttpClientUtil.postOfBody(jsonParams,url);
    }
    public AjaxResult post(String mac, AjaxResult accessToken, Datapacket<?,?> datapacket,Operation operation) throws ExecutionException, JsonProcessingException, InterruptedException {
        String body = post(mac,accessToken,datapacket);
        JsonNode jsonNode = objectMapper.readTree(body);
        String resultCode = jsonNode.at("/code").asText();
        if("100".equals(resultCode)) {//响应成功
            return AjaxResult.success()
                    .put(AjaxResult.CODE_TAG, HttpStatus.OK.value())
                    .put(AjaxResult.DATA_TAG,body);
        }
        AjaxResult result = AjaxResult.error()
                .put(AjaxResult.CODE_TAG,HttpStatus.BAD_REQUEST)
                .put(AjaxResult.DATA_TAG, operation.getDescription()+" : "+body);
        //失败记录日志
        log.error("失败: " + objectMapper.writeValueAsString(result));
        return result;
    }
    public AjaxResult postOfCache(String mac, AjaxResult accessToken, Datapacket<?,?> datapacket,Operation operation,String key) throws ExecutionException, JsonProcessingException, InterruptedException {
        String body = post(mac,accessToken,datapacket);
        JsonNode jsonNode = objectMapper.readTree(body);
        String resultCode = jsonNode.at("/code").asText();
        if("100".equals(resultCode)) {//响应成功
            Response<?> response = (Response<?>) jmsTemplate.receiveAndConvert(operation.getCode()+""+key);
            if(response != null){//成功
                return AjaxResult.success()
                        .put(AjaxResult.CODE_TAG,HttpStatus.OK.value())
                        .put(AjaxResult.DATA_TAG,response);
            }
        }
        AjaxResult result = AjaxResult.error()
                .put(AjaxResult.CODE_TAG,HttpStatus.BAD_REQUEST.value())
                .put(AjaxResult.DATA_TAG, operation.getDescription()+" : "+body);
        //失败记录日志
        log.error("失败: "+objectMapper.writeValueAsString(result));
        return result;
    }
    public AjaxResult postOfCache(String mac, AjaxResult accessToken, Datapacket<?,?> datapacket, Operation operation, List<String> keys) throws ExecutionException, JsonProcessingException, InterruptedException {
        String body = post(mac,accessToken,datapacket);
        JsonNode jsonNode = objectMapper.readTree(body);
        String resultCode = jsonNode.at("/code").asText();
        if("100".equals(resultCode)) {//响应成功
            ArrayList<Response<?>> respList = new ArrayList<>();
            keys.forEach(key -> {
                Response<?> response = (Response<?>) jmsTemplate.receiveAndConvert(operation.getCode()+"" + key);
                if(response != null) respList.add(response);
            });
            if(!respList.isEmpty()){//成功
                return AjaxResult.success()
                        .put(AjaxResult.CODE_TAG,HttpStatus.OK.value())
                        .put(AjaxResult.DATA_TAG,respList);
            }
        }
        AjaxResult result = AjaxResult.error()
                .put(AjaxResult.CODE_TAG,HttpStatus.BAD_REQUEST.value())
                .put(AjaxResult.DATA_TAG, operation.getDescription()+" : "+body);
        //失败记录日志
        log.error("失败: " + objectMapper.writeValueAsString(result));
        return result;
    }
}
