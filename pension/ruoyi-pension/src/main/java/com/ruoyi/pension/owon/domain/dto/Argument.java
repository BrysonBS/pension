package com.ruoyi.pension.owon.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.owon.convertor.OwonMillsDeserializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName(value ="owon_argument")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Argument<E,R> implements Serializable {
    @TableField(exist = false)
    private List<E> epList;
    @TableField(exist = false)
    private List<R> rlist;
    @TableField(exist = false)
    private R[] deviceList;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer dpId;

    private String mac;
    private String agentid;
    private String version;
    private String username;
    private String password;
    private String oldPassword;
    private String newPassword;
    private String timeArea;
    private String area;
    private Integer timezone;
    private Boolean dst;
    @JsonDeserialize(using = OwonMillsDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime utc;
    private Integer dhcp;
    private String staticIp;
    private String ipMask;
    private String ipGw;
    private String primaryDns;
    private String secondDns;
    private Integer total;
    private Integer start;
    private Integer count;
    private String ieee;
    private Integer ep;
    private Integer time;
    private String name;
    private String country;
    private String state;
    private String zipcode;
    private String apptoken;
    private String session;
    private String softversion;
    private Integer softversionnum;
    private String protocol;
    private String protocolversion;
    private Integer deviceType;
    private String devModel;
    private String chiptype;
    private Integer wifitype;
    private String subVersion;
    private String ssid;
    private String sskey;
    private String netMode;
    private String web;
    private String portNum;
    private String ipAddr;
    private String sslPortNum;
    private Boolean success;
    private Integer errorcodes;
    private String errormessages;
    private Integer rtype;
    private Integer zoneId;
    private Integer zoneType;
    private String zoneName;
    private Integer status;
    private Integer batteryPercentageRemain;//剩余电量200 = 100%
    private String switchgear;
    private Integer second;
    private Integer option;
    private Integer duration;
    private Integer devType;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer sleepFlag;
    private Integer statusValue;
    private Integer dataType;
    private Integer awakeFlag;
    private Integer batCharge;
    private Integer batLvl;
    private Integer netState;

    @TableField(exist = false)
    private Integer connectType;
    @TableField(exist = false)
    private String serialNo;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}