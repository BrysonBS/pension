package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import lombok.Data;

/**
 * 
 * @TableName nursing_order_setting
 */
@TableName(value ="nursing_order_setting")
@Data
public class NursingOrderSetting extends BasePensionEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 所属机构
     */
    private Long deptId;

    /**
     * 超时自动关闭
     */
    private Integer closeOvertime;

    /**
     * 完成后自动评价
     */
    private Integer commentOvertime;
    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}