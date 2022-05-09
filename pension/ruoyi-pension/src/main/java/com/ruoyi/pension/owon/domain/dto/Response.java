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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName owon_response
 */
@TableName(value ="owon_response")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<E> implements Serializable {
    @TableField(exist = false)
    private List<E> epList;
    @TableField(exist = false)
    private List<E> bleDevList;

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
    private Integer timezone;
    private Integer dstStart;
    private Integer dstEnd;
    private Integer dstShift;
    private String area;
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
    private String name;
    private String country;
    private String state;
    private String zipcode;
    private String session;
    private Integer utc0;
    private String devModel;
    private String chiptype;
    private Integer wifitype;
    private Integer deviceType;
    private String ssid;
    private String sskey;
    private String netMode;
    private Integer directLink;
    private Integer wlan;
    private Integer cloud;
    private String web;
    private String portNum;
    private String ipAddr;
    private String sslPortNum;
    private Integer versionnum;
    private String status;
    private Integer txPower;
    private Integer radioChannel;
    private Integer panId;
    private String ePid;
    private Integer cache;
    private String switchgear;
    private Integer errtype;
    private Integer zoneId;
    private Integer zoneType;
    private String zoneName;
    private Integer ep;
    private String ieee;
    private String description;
    private Integer zoneStatus;
    private Integer option;
    @JsonDeserialize(using = OwonMillsDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime startTime;
    private Integer duration;
    private Integer netState;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}