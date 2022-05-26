package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 
 * @TableName nursing_person
 */
@TableName(value ="nursing_person")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NursingPerson implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long deptId;
    private String dictLevelId;
    private String guardian;
    private String guardianPhone;
    private String name;
    private String address;
    private String remark;
    private String idCardId;
    private String medicalHistory;
    private String phone;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic(value = "0",delval = "2")
    private String delFlag;

    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Map<String, String> params;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}