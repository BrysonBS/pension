package com.ruoyi.pension.owon.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.ruoyi.pension.owon.domain.enums.Operation;
import com.ruoyi.pension.owon.domain.enums.Platform;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName(value ="owon_datapacket")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Datapacket<A,R> implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(exist = false)
    @JsonUnwrapped
    private String sjson;//序列化失败保存
    @TableField(exist = false)
    private A argument;
    @TableField(exist = false)
    private R response;

    private Platform source;
    private Platform target;
    private Operation operation;

    private Integer reportId;
    private String type;
    private String command;
    private String session;
    private Integer sequence;
    private String ip;
    private String port;
    private Boolean result;
    private String description;
    private String ieee;
    private Integer ep;
    private String switchgear;
    private Integer startNum;
    private Integer count;

    @TableField(exist = false)
    private Boolean ignore; //忽略不持久化
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}