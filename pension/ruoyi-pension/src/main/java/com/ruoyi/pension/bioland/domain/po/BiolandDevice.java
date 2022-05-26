package com.ruoyi.pension.bioland.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.ruoyi.pension.owon.domain.po.DevicePhone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName bioland_device
 */
@TableName(value ="bioland_device")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BiolandDevice implements Serializable {
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