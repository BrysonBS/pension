package com.ruoyi.pension.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeEnum {
    ZIGBEE_CONFIG("zigbeeConfig","ZigBee配置"),
    UPDATE("update","更新"),
    STATE("state","设备状态"),
    MONITOR("monitor","报警传感器状态"),
    BLE_MODULE("BleModule","BLE模式")
    ;
    private final String value;
    private final String description;
}
