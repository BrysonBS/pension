package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 
 * @TableName nursing_record
 */
@TableName(value ="nursing_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NursingRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String orderNumber;
    @NotNull(message = "申请人必填")
    private Long userId;
    @NotBlank(message = "申请人必填")
    private String applyName;
    @NotBlank(message = "电话必填")
    private String phone;
    @NotNull(message = "所属部门必填")
    private Long deptId;
    @NotBlank(message = "所属部门必填")
    private String deptName;
    @NotBlank(message = "护理级别必填")
    private String dictLevelId;
    @NotNull(message = "申请时间必填")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime applyTime;
    @NotNull(message = "开始时间必填")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime beginTime;
    @NotNull(message = "结束时间必填")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;
    @NotNull(message = "护理人员必填")
    private Integer personId;
    @NotBlank(message = "护理人员必填")
    private String personName;
    private String remark;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic(value = "0",delval = "2")
    private String delFlag;

    @NotNull(message = "护理项目必填")
    @TableField(exist = false)
    @JsonInclude
    private String[] dictServiceIds;
    @TableField(exist = false)
    @JsonInclude
    private List<PensionUpload> uploads;
    @TableField(exist = false)
    private Map<String, String> params;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}