package com.ruoyi.pension.common.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.pension.tuya.api.TuyaMqttClient;
import com.ruoyi.pension.tuya.api.TuyaMqttClientManager;
import com.ruoyi.pension.tuya.domain.MqttConnectInfo;
import com.ruoyi.pension.tuya.domain.WebRTCInfo;
import com.ruoyi.pension.tuya.domain.message.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.function.BiFunction;

@Component
public class CameraHandler implements BiFunction<LoginUser, JsonNode, AjaxResult> {
    @Autowired
    private TuyaMqttClientManager tuyaMqttClientManager;

    @Override
    public AjaxResult apply(LoginUser loginUser, JsonNode jsonNode) {
        String type = jsonNode.at("/type").asText();
        String deviceId = jsonNode.at("/deviceId").asText();
        String uid = jsonNode.at("/uid").asText();
        if(Strings.isBlank(deviceId)) return AjaxResult.success();

        TuyaMqttClient tuyaMqttClient = tuyaMqttClientManager.getTuyaMqttClient(uid);
        MqttConnectInfo mqttConnectInfo = tuyaMqttClient.getMqttConnectInfo();
        WebRTCInfo webRTCInfo = tuyaMqttClientManager.getWebRTCInfo(deviceId,mqttConnectInfo);

        String payload = jsonNode.at("/payload").asText();
        MqttDataMsg msg  = switch (type){
            case "configs" -> null;
            case "candidate" -> MqttDataMsg.candidateBuilder()
                    .candidate(payload)
                    .build();
            case "offer" -> MqttDataMsg.offerBuilder()
                    .sdp(payload)
                    .auth(webRTCInfo.getAuth())
                    .build();
            case "answer" -> MqttDataMsg.answerBuilder()
                    .sdp(payload)
                    .build();
            case "disconnect" -> MqttDataMsg.disconnectBuilder().build();
            default -> throw new RuntimeException("mqtt请求失败! 未知类型: " + type);
        };
        TuyaMqttMessage tuyaMqttMessage = TuyaMqttMessage.builder()
                .operate(jsonNode.at("/operate").asText())
                .user(loginUser)
                .type(type)
                .mqttPayload(MqttPayload.builder()
                        .t(Instant.now().getEpochSecond())
                        .data(MqttData.builder()
                                .header(MqttDataHeader.builder()
                                        .type(type)
                                        .from(mqttConnectInfo.getMqttUid())
                                        .to(deviceId)
                                        .sessionid(jsonNode.at("/sessionId").asText())
                                        .subDevId("")
                                        .motoId(webRTCInfo.getMotoId())
                                        .tid("")
                                        .build())
                                .msg(msg)
                                .build()
                        )
                        .build())
                .build();
        tuyaMqttClient.publish(tuyaMqttMessage,webRTCInfo.getTopic());

        return "configs".equals(type) ? AjaxResult.success()
                .put(AjaxResult.OPERATE_TAG, jsonNode.at("/operate").asText())
                .put(AjaxResult.DATA_TAG, Map.of("type",type,"data",webRTCInfo.getIces())) :
                AjaxResult.success();
    }
}
