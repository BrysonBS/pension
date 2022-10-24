package com.ruoyi.pension.tuya.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class MqttConnectInfo {
    private String uid;
    private String url;
    @JsonProperty("client_id")
    private String clientId;
    private String username;
    private String password;
    private String sinkTopicIpc;
    private String sourceTopicIpc;
    private String mqttUid;
    @JsonProperty("expire_time")
    private Long expireTime;

    @JsonIgnore
    private Long createTimeSeconds;
    @JsonIgnore
    private String linkId;
}
