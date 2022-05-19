package com.ruoyi.pension.owon.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operation {

    /** 系统上报 */

    /** OWON自动上报2XXXX */
    REPORT_PANIC_BUTTON(20000,"拉绳报警器上报信息"),
    REPORT_SENSOR_UPDATE(20010,"安防上报"),
    REPORT_SENSOR_WARNING(20020,"安防报警上报"),
    REPORT_BATTERY_PERCENTAGE(20030,"电量上报"),
    REPORT_EP_LIST(20040,"EP节点列表更新上报"),
    REPORT_HEART_BEAT(20050,"心跳同步上报"),
    REPORT_BLE_DATA_UPDATE(20060,"BLE设备上报更新"),
    REPORT_BLE_CONNECT_CHANGE(20070,"BLE设备连接状态变化上报"),
    REPORT_FALL_DETECT_NOTIFY(20080,"跌倒报警器信息上报"),

    /** BIOLAND自动上报22XXX */
    REPORT_BLOOD_PRESSURE(22000,"血压计上报信息"),
    REPORT_BLOOD_GLUCOSE(22010,"血糖仪上报信息"),

    /** 请求3XXXX */
    REQUEST_EP_LIST(30040,"EP节点列表请求"),
    REQUEST_PERMIT_JOIN(30050,"设备加网请求"),
    REQUEST_DELETE_ZB(30060,"移除在网设备请求"),
    REQUEST_RENAME_ZB(30070,"设置设备节点名称请求"),
    REQUEST_STATE_ZB(30080,"获取设备当前状态请求"),
    REQUEST_GET_WARNING(30090,"获取报警传感器状态请求"),
    REQUEST_START_WARNING(30100,"开启/关闭报警器请求"),
    REQUEST_BLE_LIST(30110,"BLE设备列表请求"),
    REQUEST_BLE_SCAN(30120,"BLE设备加网请求"),
    /** 返回4XXXX */
    RESULT_EP_LIST(40040,"EP节点列表请求返回"),
    RESULT_PERMIT_JOIN(40050,"设备加网请求返回"),
    RESULT_DELETE_ZB(40060,"移除在网设备请求返回"),
    RESULT_RENAME_ZB(40070,"设置设备节点名称返回"),
    RESULT_STATE_ZB(40080,"获取设备当前状态返回"),
    RESULT_GET_WARNING(40090,"获取报警传感器状态返回"),
    RESULT_START_WARNING(40100,"开启/关闭报警器请求返回"),
    RESULT_BLE_LIST(40110,"BLE设备列表请求返回"),
    RESULT_BLE_SCAN(40120,"BLE设备加网请求"),
    ;
    @EnumValue
    private Integer code;
    private String description;
}
