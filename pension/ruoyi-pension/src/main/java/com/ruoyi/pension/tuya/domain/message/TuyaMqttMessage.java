package com.ruoyi.pension.tuya.domain.message;

import com.ruoyi.common.core.domain.model.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TuyaMqttMessage {
    private LoginUser user;
    private String operate;
    private String type;
    private MqttPayload mqttPayload;
}
