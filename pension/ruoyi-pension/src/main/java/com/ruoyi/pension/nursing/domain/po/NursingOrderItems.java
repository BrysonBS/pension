package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName nursing_order_items
 */
@TableName(value ="nursing_order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NursingOrderItems implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer orderId;

    /**
     * 护理项目
     */
    private String dictLabel;

    /**
     * 
     */
    private String dictValue;

    /**
     * 原价
     */
    private BigDecimal price;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 折后价
     */
    private BigDecimal discountPrice;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}