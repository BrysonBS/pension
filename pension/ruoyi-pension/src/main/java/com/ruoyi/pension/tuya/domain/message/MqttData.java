package com.ruoyi.pension.tuya.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//mqtt消息帧
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqttData {
    private MqttDataHeader header;
    // mqtt消息体，可为offer candidate answer disconnect
    private MqttDataMsg msg;
}
