package com.ruoyi.pension.common.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeChatPayConfig {
    /** 服务号id */
    private String appId;
    /** 商户号 */
    private String merchantId;
    /** 微信支付 APIv3 密钥 */
    private String apiV3Key;
    /** 微信支付平台证书根路径 */
    private String wechatPayCertificatePath;
    /** 商户API私钥路径: apiclient_key.pem */
    private String privateKeyPath;
    private String privateKeyContent;
    /** 商户证书序列号 */
    private String merchantSerialNumber;
    /** 微信支付V3-url前缀 */
    private String gateway;
    /** 支付异步回调地址 */
    private String notifyUrl;
    /** 退款通知回调 */
    private String refundNotifyUrl;
    /** 归属部门 */
    private String deptName;
}
