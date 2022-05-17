package com.ruoyi.pension.owon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.ruoyi.pension.owon.domain.enums.Platform;
import lombok.Data;

/**
 * 
 * @TableName owon_device_phone
 */
@TableName(value ="owon_device_phone")
@Data
public class DevicePhone implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer deviceId;
    private String phone;
    private Platform source;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}