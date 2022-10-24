package com.ruoyi.pension.tuya.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.tuya.connector.MqttConnector;
import com.ruoyi.pension.tuya.domain.MqttConnectInfo;
import com.ruoyi.pension.tuya.domain.WebRTCInfo;
import com.tuya.connector.api.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class TuyaMqttClientManager {
    @Autowired
    private MqttConnector mqttConnector;
    @Autowired
    private RedisCache redisCache;
    private static final ConcurrentHashMap<String,TuyaMqttClient> MAP = new ConcurrentHashMap<>();
    public TuyaMqttClient getTuyaMqttClient(String uid){
        TuyaMqttClient tuyaMqttClient = MAP.computeIfAbsent(uid,unused -> new TuyaMqttClient(uid));
        tuyaMqttClient.restart();
        return tuyaMqttClient;
    }
    public MqttConnectInfo getMqttConnectInfo(String uid){
        String linkId = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        System.out.println("linkId: " + linkId);
        HashMap<String,String> bodyMap = new HashMap<>();
        bodyMap.put("uid",uid);
        bodyMap.put("link_id", linkId);
        bodyMap.put("link_type","mqtt");
        bodyMap.put("topics","ipc");
        Result<JsonObject> result = mqttConnector.getMqttConfig(bodyMap);
        if(!result.getSuccess()) {
            log.error(result.getMsg(), result);
            throw new RuntimeException(result.getMsg());
        }

        JsonObject body = result.getResult();
        String sourceTopicIpc = body.getAsJsonObject("source_topic").get("ipc").getAsString();
        long expireTime = body.get("expire_time").getAsLong();
        //返回
        return MqttConnectInfo.builder()
                .createTimeSeconds(Instant.now().getEpochSecond())
                .linkId(linkId)
                .uid(uid)
                .url(body.get("url").getAsString())
                .clientId(body.get("client_id").getAsString())
                .username(body.get("username").getAsString())
                .password(body.get("password").getAsString())
                .sinkTopicIpc(body.getAsJsonObject("sink_topic").get("ipc").getAsString())
                .sourceTopicIpc(sourceTopicIpc)
                .mqttUid(sourceTopicIpc.substring(sourceTopicIpc.lastIndexOf('/') + 1))
                .expireTime(expireTime)
                .build();
    }
    public WebRTCInfo getWebRTCInfo(String deviceId, MqttConnectInfo mqttConnectInfo){
        //先从缓存中查找
        WebRTCInfo webRTCInfo = redisCache.getCacheObject(PensionBusiness.getKeyOfWebRTCInfo(deviceId));
        if(webRTCInfo != null) return webRTCInfo;
        //否则从iot平台获取
        Result<JsonObject> result = mqttConnector.getWebRTCInfo(mqttConnectInfo.getUid(),deviceId);
        //不成功,则记录日志
        if(!result.getSuccess()) {
            log.error(result.getMsg(), result);
            throw new RuntimeException(result.getMsg());
        }
        JsonObject body = result.getResult();
        String motoId = body.get("moto_id").getAsString();
        JsonArray ices = body.getAsJsonObject("p2p_config").getAsJsonArray("ices");
        int ttl = StreamSupport.stream(ices.spliterator(),true)
                .map(a -> {
                    JsonElement ice = a.getAsJsonObject().get("ttl");
                    return ice == null ? 3600 : ice.getAsInt();
                })
                .min(Comparator.naturalOrder())
                .orElse(0);
        webRTCInfo = WebRTCInfo.builder()
                .id(body.get("id").getAsString())
                .auth(body.get("auth").getAsString())
                .motoId(motoId)
                .ices(ices.toString())
                .topic(mqttConnectInfo.getSinkTopicIpc()
                        .replace("moto_id",motoId)
                        .replace("{device_id}",deviceId))
                .ttl(ttl)
                .build();
        redisCache.setCacheObject(PensionBusiness.getKeyOfWebRTCInfo(deviceId),webRTCInfo, webRTCInfo.getTtl(), TimeUnit.SECONDS);
        return webRTCInfo;
    }
}
