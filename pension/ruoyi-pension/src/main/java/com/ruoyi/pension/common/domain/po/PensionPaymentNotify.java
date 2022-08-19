package com.ruoyi.pension.common.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

/**
 * 
 * @TableName pension_payment_notify_alipay
 */
@TableName(value ="pension_payment_notify")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensionPaymentNotify implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonAlias({"gmt_payment","gmtPayment"})
    private LocalDateTime gmtPayment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonAlias({"notify_time","notifyTime"})
    private LocalDateTime notifyTime;
    private String subject;
    @JsonAlias({"buyer_id","buyerId"})
    private String buyerId;
    @JsonAlias({"invoice_amount","invoiceAmount"})
    private BigDecimal invoiceAmount;
    private String version;
    @JsonAlias({"notify_id","notifyId"})
    private String notifyId;
    @JsonAlias({"notify_type","notifyType"})
    private String notifyType;
    @JsonAlias({"out_trade_no","outTradeNo"})
    private String outTradeNo;
    @JsonAlias({"total_amount","totalAmount"})
    private BigDecimal totalAmount;
    @JsonAlias({"trade_status","tradeStatus"})
    private String tradeStatus;
    @JsonAlias({"trade_no","tradeNo"})
    private String tradeNo;
    @JsonAlias({"auth_app_id","authAppId"})
    private String authAppId;
    @JsonAlias({"receipt_amount","receiptAmount"})
    private BigDecimal receiptAmount;
    @JsonAlias({"point_amount","pointAmount"})
    private BigDecimal pointAmount;
    @JsonAlias({"app_id","appId"})
    private String appId;
    @JsonAlias({"buyer_pay_amount","buyerPayAmount"})
    private BigDecimal buyerPayAmount;
    @JsonAlias({"sign_type","signType"})
    private String signType;
    @JsonAlias({"seller_id","sellerId"})
    private String sellerId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonAlias({"gmt_create","gmtCreate"})
    private LocalDateTime gmtCreate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS",timezone = "GMT+8")
    @JsonAlias({"gmt_refund","gmtRefund"})
    private LocalDateTime gmtRefund;
    private String charset;
    @JsonAlias({"out_biz_no","outBizNo"})
    private String outBizNo;
    @JsonAlias({"refund_fee","refundFee"})
    private BigDecimal refundFee;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}