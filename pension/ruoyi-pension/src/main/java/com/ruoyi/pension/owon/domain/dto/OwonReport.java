package com.ruoyi.pension.owon.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.ruoyi.pension.owon.convertor.OwonMillsDeserializer;
import com.ruoyi.pension.owon.convertor.OwonReportSjsonDeserializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@TableName(value ="owon_report")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwonReport implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @NotBlank
    private String code;
    @NotBlank
    private String mac;
    @JsonDeserialize(using = OwonMillsDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime ts;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime created;


    //忽略字段
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    @TableField(exist = false)
    @JsonDeserialize(using = OwonReportSjsonDeserializer.class)
    private Datapacket sjson;
    @TableField(exist = false)
    @Pattern(regexp = "kIt-79JOk5",message = "token校验失败")
    private String token;
}