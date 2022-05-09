package com.ruoyi.pension.owon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName owon_device_ep
 */
@TableName(value ="owon_device_ep")
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceEp implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String ieee;
    private Integer ep;
    private Integer netDeviceType;
    private Boolean linkStatus;
    private Integer deviceType;
    @JsonProperty("IASZoneType")
    private Integer iasZoneType;
    @JsonProperty("ProfileId")
    private Integer profileId;
    @TableField("`name`")
    private String name;
    private String devModel;
    private Integer clusterFlag;
    private Integer manuCode;
    @TableField("`option`")
    private Integer option;
    private Integer duration;
    private Integer netState;

    @TableField(exist = false)
    private String searchName;
    @TableField(exist = false)
    private String gwCode;
    @TableField(exist = false)
    private Integer devtype;
    @TableField(exist = false)
    private Integer sequence;
    @TableField(exist = false)
    private Integer cache;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}