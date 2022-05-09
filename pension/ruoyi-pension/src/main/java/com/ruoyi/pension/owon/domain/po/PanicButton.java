package com.ruoyi.pension.owon.domain.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.owon.convertor.OwonMillsDeserializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

/**
 * 
 * @TableName owon_panic_button
 */
@TableName(value ="owon_panic_button")
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PanicButton implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer argId;

    @JsonDeserialize(using = OwonMillsDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime utc;
    private String name;
    private Integer alarm;
    private Integer zoneType;
    private Integer astatus;
    private Integer zoneId;
    private Integer ep;
    private String zoneName;
    private Integer idx;
    private String ieee;
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}