package com.ruoyi.pension.bioland.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import com.ruoyi.pension.owon.domain.po.DevicePhone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName bioland_device
 */
@TableName(value ="bioland_device",excludeProperty = {"createBy","createTime","updateBy","updateTime"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiolandDevice extends BasePensionEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long deptId;
    private Integer categoriesId;
    private String delFlag;
    private String name;
    private String serialNumber;


    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private DevicePhone[] phones;
    @TableField(exist = false)
    private Integer[] phonesId;
    @TableField(exist = false)
    private String deptFullName;
    @TableField(exist = false)
    private String typeName;
    @TableField(exist = false)
    private String deviceType;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}