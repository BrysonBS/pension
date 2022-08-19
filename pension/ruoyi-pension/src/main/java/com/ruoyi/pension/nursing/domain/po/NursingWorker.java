package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName nursing_worker
 */
@TableName(value ="nursing_worker")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NursingWorker extends BasePensionEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long userId;
    private Long deptId;
    private String name;
    private String gender;
    private String phone;
    /**
     * 状态(0->正常,1->停用,2->接单中)
     */
    private String status;
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic(value = "0",delval = "2")
    private String delFlag;

    @TableField(exist = false)
    private List<String> statusList;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private Map<String, String> params;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}