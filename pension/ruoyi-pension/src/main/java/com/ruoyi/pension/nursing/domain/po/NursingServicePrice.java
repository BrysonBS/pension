package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

/**
 * 
 * @TableName nursing_service_price
 */
@TableName(value ="nursing_service_price")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NursingServicePrice extends BasePensionEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long deptId;

    /**
     * 
     */
    private String dictValue;

    /**
     * 
     */
    private BigDecimal price;

    /**
     * 价格有效期:开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime periodStart;

    /**
     * 价格有效期:结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime periodEnd;

    /**
     * 打折
     */
    @DecimalMax(value = "10",message = "折扣不能大于10!")
    @DecimalMin(value = "0",message = "折扣不能小于0!")
    private BigDecimal discount;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    @JsonAlias({"dict_label","dictLabel"})
    private String dictLabel;
    /** 折后价 */
    @TableField(exist = false)
    private BigDecimal discountPrice;
    @TableField(exist = false)
    @JsonAlias({"dept_name","deptName"})
    private String deptName;
    @TableField(exist = false)
    private String[] dictValues;
    @TableField(exist = false)
    private Map<String, String> params;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}