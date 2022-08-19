package com.ruoyi.pension.common.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @TableName pension_payment_notify_wechat
 */
@TableName(value ="pension_payment_notify_wechat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensionPaymentNotifyWechat implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String mchid;
    private String appid;
    private String outTradeNo;
    private String transactionId;
    private String tradeType;
    private String tradeState;
    private String tradeStateDesc;
    private String bankType;
    private String attach;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime successTime;
    private String openid;
    private Long total;
    private Long payerTotal;
    private String currency;
    private String payerCurrency;
    private String notifyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;
    private String eventType;
    private String resourceType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}