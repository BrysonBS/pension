package com.ruoyi.pension.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Command {
    EP_LIST("epList","获取设备EP节点列表"),
    GET_BLE_LIST("getBleList","获取BLE设备列表"),
    PERMIT_JOIN("permitJoin","允许/禁止设备加网"),
    DELETE("delete","移除在网设备"),
    RENAME("rename","设置设备节点名称"),
    MULTIDEV("multidev","获取设备当前状态"),
    GET_WARNING("getWarning","获取报警传感器状态"),
    START_WARNING("startWarning","开启关闭报警器"),
    SCAN_BLE_DEVICE("scanBleDevice","BLE设备加入连接")
    ;
    private final String value;
    private final String description;
}
