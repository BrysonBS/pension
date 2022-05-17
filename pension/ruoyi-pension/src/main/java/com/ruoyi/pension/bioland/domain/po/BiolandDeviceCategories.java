package com.ruoyi.pension.bioland.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName bioland_device_categories
 */
@TableName(value ="bioland_device_categories")
@Data
public class BiolandDeviceCategories implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String modelCode;
    private String deviceType;
    private LocalDateTime created;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}