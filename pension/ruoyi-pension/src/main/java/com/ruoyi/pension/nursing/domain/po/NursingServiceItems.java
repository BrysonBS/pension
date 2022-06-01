package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName nursing_service_items
 */
@TableName(value ="nursing_service_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NursingServiceItems implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer masterId;

    /**
     * 
     */
    private String masterName;

    /**
     * 
     */
    private Integer relateId;

    /**
     * 
     */
    private String serviceId;

    /**
     * 
     */
    private String serviceName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}