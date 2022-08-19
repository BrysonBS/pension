package com.ruoyi.pension.common.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlipayConfig {
    /** 应用id */
    private String appId;
    /** 应用私钥 */
    private String appPrivateKey;
    /** 证书模式 */
    private Boolean certModel;
    /** 支付宝公钥 */
    private String alipayPublicKey;
    /** 支付宝支付平台证书根路径 */
    private String alipayCertificatePath;
    /** 支付宝公钥证书路径 */
    private String alipayCertPublicKeyPath;
    private String alipayCertPublicKeyContent;
    /** 支付宝根证书路径 */
    private String alipayRootCertPath;
    private String alipayRootCertContent;
    /** 应用公钥证书路径 */
    private String appCertPublicKeyPath;
    private String appCertPublicKeyContent;


    /** 地址 */
    private String gateway;
    /** 签名方式 */
    private String signType;
    /** 异步回调URL */
    private String notifyUrl;
    /** 页面跳转同步回调URL */
    private String returnUrl;

    /** 超时时间 */
    private Integer timeout;
    /** 时间单位 */
    private TimeUnit timeUnit;

    /** 归属部门名称 */
    private String deptName;
}
