package com.ruoyi.pension.common.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName pension_refund_alipay
 */
@TableName(value ="pension_refund_alipay")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensionRefundAlipay implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long userId;
    private Long deptId;
    /**
     * 退款单号
     */
    private String refundNo;
    private String tradeNo;
    private String outTradeNo;
    private String code;
    private String msg;
    private String buyerLogonId;
    private String buyerUserId;
    private LocalDateTime gmtRefundPay;
    /**
     * 退款订单金额
     */
    private BigDecimal refundFeeAmount;
    /**
     * 实际退款金额
     */
    private BigDecimal refundFeeReal;
    /**
     * 订单退款总金额
     */
    private BigDecimal refundFeeTotal;
    private Boolean success;
    private String errorcode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}