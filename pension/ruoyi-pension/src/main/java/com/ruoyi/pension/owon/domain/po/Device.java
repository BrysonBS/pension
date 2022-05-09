package com.ruoyi.pension.owon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName owon_device
 */
@TableName(value ="owon_device")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long deptId;
    private String gwCode;
    private String ieee;
    private Integer ep;
    private Integer netDeviceType;
    private Boolean linkStatus;
    private Integer deviceType;
    private Integer iasZoneType;
    private Integer profileId;
    @TableField("`name`")
    private String name;
    private String devModel;
    private Integer clusterFlag;
    private Integer manuCode;
    @TableField("`option`")
    private Integer option;
    private Integer duration;
    private String status;
    private String delFlag;
    private Integer netState;
    private Integer categoriesId;

    @TableField(exist = false)
    private Integer cid;
    @TableField(exist = false)
    private String displayName;
    @TableField(exist = false)
    private DevicePhone[] phones;
    @TableField(exist = false)
    private Integer[] phonesId;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private String gwName;
    @TableField(exist = false)
    private String deptFullName;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}