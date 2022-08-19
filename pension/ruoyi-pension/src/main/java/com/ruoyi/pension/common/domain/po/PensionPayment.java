package com.ruoyi.pension.common.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName pension_payment
 */
@TableName(value ="pension_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensionPayment extends BasePensionEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long userId;
    private Long deptId;
    private String status;
    /**
     * 1->支付宝,2->微信
     */
    private Integer payType;
    private String appId;
    private String merchantId;
    private String merchantKey;
    private String merchantKeyV3;
    private String slAppId;
    private String slMerchantId;
    private String payUrl;
    private String queryUrl;
    private String returnUrl;
    private String notifyUrl;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String charset;
    private String signType;
    /**
     * 数据格式:json,xml
     */
    private String format;
    /**
     * 证书模式OR公钥模式
     * 0=否,1=是
     */

    private Boolean certModel;
    private String appCertPath;
    private String appCertContent;
    private String alipayCertPath;
    private String alipayCertContent;
    private String alipayRootCertPath;
    private String alipayRootCertContent;
    /**
     * API 证书中的 key.pem
     */
    private String wechatpayKeyPemPath;
    /**
     * API 证书中的 cert.pem
     */
    private String wechatpayCertPemPath;
    /** API证书中的 cert.p12 */
    private String wechatpayCertP12Path;
    /** API证书的存储根路径 */
    private String wechatpayRootCertPath;
    /** 商户证书序列号 */
    private String merchantSerialNumber;
    /** APIv3密钥 */
    private String apiV3Key;
    /** 微信退款通知地址 */
    private String refundNotifyUrl;

    private String remark;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic(value = "0",delval = "2")
    private String delFlag;

    @TableField(exist = false)
    private Map<String, String> params;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}