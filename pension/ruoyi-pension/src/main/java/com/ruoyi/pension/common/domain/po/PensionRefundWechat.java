package com.ruoyi.pension.common.domain.po;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName pension_refund_wechat
 */
@TableName(value ="pension_refund_wechat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensionRefundWechat implements Serializable {
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
    private String outTradeNo;

    /**
     * 
     */
    private String transactionId;

    /**
     * 
     */
    private String outRefundNo;

    /**
     * 
     */
    private String refundId;

    /**
     * 
     */
    private String refundStatus;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime successTime;

    /**
     * 订单总金额
     */
    private Long total;

    /**
     * 退款金额
     */
    private Long refund;

    /**
     * 用户实际支付金额
     */
    private Long payerTotal;

    /**
     * 用户实际退款金额
     */
    private Long payerRefund;

    /**
     * 退款入账账户
     */
    private String userReceivedAccount;

    /**
     * 退款渠道
     */
    private String channel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}