package com.ruoyi.pension.owon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 
 * @TableName owon_gateway
 */
@TableName(value ="owon_gateway")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gateway implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long deptId;
    private String gwName;
    @NotBlank(message = "网关mac地址不能为空")
    private String gwCode;
    private Integer orderNum;
    private String leader;
    private String phone;
    private String email;
    private String status;
    private String delFlag;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}