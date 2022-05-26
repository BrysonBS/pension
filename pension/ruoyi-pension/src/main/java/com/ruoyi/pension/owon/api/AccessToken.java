package com.ruoyi.pension.owon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.config.OwonProps;
import com.ruoyi.pension.owon.convertor.OwonTsConvertor;
import com.ruoyi.pension.owon.utils.HttpClientUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

@Component
public class AccessToken {
    @Autowired
    private OwonProps owonProps;
    @Autowired
    private ObjectMapper objectMapper;
    public AjaxResult getAccessToken() throws ExecutionException, InterruptedException, JsonProcessingException {
        long ts = OwonTsConvertor.getNowTs();
        if(Strings.isBlank(owonProps.getAccessToken()) || ts - 86000 > owonProps.getTs()){
            //否则获取新的token
            owonProps.setTs(ts);//设置查询时间戳
            String url = owonProps.getServer_china()+ owonProps.getUri_accessToken_get();
            String jsonParams = owonProps.getRequestJson();
            //发送获取token请求
            HttpResponse<String> httpResponse = HttpClientUtil.post(jsonParams,url);
            JsonNode jsonNode = objectMapper.readTree(httpResponse.body());
            String resultCode = jsonNode.at("/code").asText();
            String resultToken = jsonNode.at("/accessToken").asText();
            long resultTs = jsonNode.at("/ts").asLong();
            if("100".equals(resultCode)){//成功
                owonProps.setAccessToken(resultToken);
                owonProps.setTs(resultTs);
                owonProps.setRefreshToken(jsonNode.at("/refreshToken").asText());
            }
            else{
                return AjaxResult.error()
                        .put(AjaxResult.CODE_TAG,HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .put(AjaxResult.DATA_TAG,"获取owon的AccessToken失败:  "+httpResponse.body());
            }
        }
        return AjaxResult.success()
                .put(AjaxResult.CODE_TAG,HttpStatus.OK.value())
                .put(AjaxResult.DATA_TAG,owonProps.getAccessToken())
                .put("ts",owonProps.getTs());
    }

    public AjaxResult refreshToken() throws JsonProcessingException, ExecutionException, InterruptedException {
        long ts = OwonTsConvertor.getNowTs();
        owonProps.setTs(ts);//设置查询时间戳
        String url = owonProps.getServer_china()+ owonProps.getUri_accessToken_refresh();
        String jsonParams = owonProps.getRefreshRequestJson();
        //发送获取token请求
        HttpResponse<String> httpResponse = HttpClientUtil.post(jsonParams,url);
        JsonNode jsonNode = objectMapper.readTree(httpResponse.body());
        String resultCode = jsonNode.at("/code").asText();
        String resultToken = jsonNode.at("/accessToken").asText();
        long resultTs = jsonNode.at("/ts").asLong();
        if("100".equals(resultCode)){//成功
            owonProps.setAccessToken(resultToken);
            owonProps.setTs(resultTs);
        }
        else{
            return AjaxResult.error()
                    .put(AjaxResult.CODE_TAG,HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .put(AjaxResult.DATA_TAG,"刷新owon的AccessToken失败:  "+httpResponse.body());
        }
        return AjaxResult.success()
                .put(AjaxResult.CODE_TAG,HttpStatus.OK.value())
                .put(AjaxResult.DATA_TAG,owonProps.getAccessToken());
    }
}
