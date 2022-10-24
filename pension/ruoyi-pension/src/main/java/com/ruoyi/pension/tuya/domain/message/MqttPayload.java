package com.ruoyi.pension.tuya.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqttPayload {
    // mqtt消息的协议号，webRTC属于实时流服务，为302
    @Builder.Default
    private Integer protocol = 302;
    // 通讯协议版本号
    @Builder.Default
    private String pv = "2.2";
    // Unix时间戳，单位为second
    private Long t;
    private MqttData data;
}
