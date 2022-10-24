package com.ruoyi.pension.tuya.domain.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//mqtt消息帧头
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqttDataHeader {
    // mqtt消息类型，offer candidate answer disconnect
    private String type;
    // mqtt消息发送方
    private String from;
    // mqtt消息接受方
    private String to;
    // 如果发送方或接收方是设备，且是子设备，这里为子设备id
    @JsonProperty("sub_dev_id")
    private String subDevId;
    // mqtt消息所属的会话id
    private String sessionid;
    // mqtt消息相关的信令服务moto的id
    @JsonProperty("moto_id")
    private String motoId;
    // 事务id，MQTT控制信令透传时携带
    private String tid;
}
