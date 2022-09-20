package com.ruoyi.pension.common.domain.consts;

import com.ruoyi.pension.common.domain.po.PensionPaymentNotify;

public class PensionBusiness {
    //护理记录
    public static String NURSING_RECORD = "NR";
    //pension模块文件上传获取文件名的key
    public static String PENSION_UPLOAD_KEY = "";
    //护理记录存储路径
    public static String NURSING_RECORD_ROOT = "nursing/record/";
    //护工证书存储路径
    public static String NURSING_WORKER_CERTIFICATE_ROOT = "nursing/worker/certificate/";

    public static String MERCHANT_CERT_ROOT = "/merchant/";

    public static String PENSION_PAYMENT = "PENSION_PAYMENT";

    /** 通知相关 */
    public static String NOTIFICATION = "NOTIFICATION";
    public static String OWON_SLEEPACE = "OWON_SLEEPACE";//睡眠带通知
    public static String BIOLAND_BLOOD_PRESSURE = "BIOLAND_BLOOD_PRESSURE";//血压计通知
    public static String BIOLAND_BLOOD_GLUCOSE = "BIOLAND_BLOOD_GLUCOSE";//血糖仪通知

    /**
     * 支付宝支付配置类缓存key
     * @param deptId 机构ID
     * @return
     */
    public static String getKeyOfAliConfig(Long deptId){
        return PENSION_PAYMENT + deptId + ":1";
    }

    /**
     * 获取微信支付配置类缓存key
     * @param deptId 机构ID
     * @return
     */
    public static String getKeyOfWeChatConfig(Long deptId){
        return PENSION_PAYMENT + deptId + ":2";
    }
    //支付订单前缀
    public static String PAYMENT_ORDER_PREFIX = "PO";
    //退款订单前缀
    public static String REFUND_ORDER_PREFIX = "RO";

    /**
     * 付款,退款缓存key缓存前缀:
     */
    public static String PAYMENT = "PAYMENT";
    public static String REFUND = "REFUND";
    /**
     * 生成付款或者退款缓存key
     * @return
     */
    public static String getKeyOfPayment(PensionPaymentNotify pensionPaymentNotify){
        //先缓存
        String key = pensionPaymentNotify.getOutTradeNo() + ":" +
                pensionPaymentNotify.getTradeNo() + ":" +
                pensionPaymentNotify.getTotalAmount();
        //付款通知
        if(pensionPaymentNotify.getGmtRefund() == null || pensionPaymentNotify.getRefundFee() == null)
            return PAYMENT + ":" + key;
        else
            return REFUND + ":" + key;
    }

    /** 订单设置 */
    public static String ORDER_SETTING = "ORDER_SETTING";
    public static String getKeyOfOrderSetting(Long deptId){
        return ORDER_SETTING + ":" + deptId;
    }

    /** 服务项目价格列表 */
    public static String SERVICE_PRICE = "NURSING_SERVICE_PRICE";
    public static String getKeyOfServicePrice(Long deptId){
        return SERVICE_PRICE + ":" + deptId;
    }


}
