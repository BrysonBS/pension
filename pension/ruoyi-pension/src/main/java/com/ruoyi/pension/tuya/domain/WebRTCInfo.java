package com.ruoyi.pension.tuya.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebRTCInfo {
    private String auth;
    private String id;
    @JsonProperty("moto_id")
    private String motoId;
    private String ices;
    private String topic;
    private Integer ttl;//过期时间:秒
}
