package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import lombok.*;

/**
 * 
 * @TableName nursing_person
 */
@TableName(value ="nursing_person")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NursingPerson extends BasePensionEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long userId;
    private Long deptId;
    private String dictDisabilityLevel;
    private String dictLevelId;
    private String guardian;
    private String guardianPhone;
    private String name;
    private String province;
    private String city;
    private String district;
    private String address;
    private String detailAddress;
    private BigDecimal lat;
    private BigDecimal lng;
    private String remark;
    private String idCardId;
    private String medicalHistory;
    private String phone;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic(value = "0",delval = "2")
    private String delFlag;


    @TableField(exist = false)
    private LoginUser loginUser;
    @TableField(exist = false)
    private String dictDisableLabel;
    @TableField(exist = false)
    private String fullAddress;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}