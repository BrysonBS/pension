package com.ruoyi.pension.owon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 
 * @TableName owon_device_categories
 */
@TableName(value ="owon_device_categories")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceCategories implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String displayName;
    private String devModel;
    private Integer deviceType;
    private Integer iasZoneType;
    private String serverDevModel;
    private String displayNameUs;
    private Integer type;

    @TableField(exist = false)
    private String info;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}