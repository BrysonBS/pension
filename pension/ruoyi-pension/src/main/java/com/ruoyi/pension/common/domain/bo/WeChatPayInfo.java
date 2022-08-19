package com.ruoyi.pension.common.domain.bo;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class WeChatPayInfo {
    private Long userId;
    private Long deptId;
    private String tradeNo;
    private String subject;
    /** 单位: 元 */
    private BigDecimal totalAmount;
    private String type;
    private String ip;
    private String openid;
    private String h5Info;
    private String gateway;
    private String uri;

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        public static String H5INFO_IOS = "IOS";
        public static String H5INFO_ANDROID = "ANDROID";
        public static String H5INFO_WAP = "WAP";
        public static String TYPE_NATIVE = "native";
        public static String TYPE_APP = "app";
        public static String TYPE_H5 = "h5";
        public static String TYPE_JSAPI = "jsapi";
        /** 小程序jsapi */
        public static String TYPE_SUB_JSAPI = "sub_jsapi";
        /** Native下单 */
        public static String URI_PAY_TRANSACTIONS = "/pay/transactions/";
        /** 查询订单 */
        public static String URI_ORDER_QUERY_BY_NO = "/pay/transactions/out-trade-no/";
        /** 关闭订单 */
        public static String URI_CLOSE_ORDER_BY_NO = "/pay/transactions/out-trade-no/%s/close";
        /** 申请退款 */
        public static String URI_DOMESTIC_REFUNDS = "/refund/domestic/refunds";
        /** 查询单笔退款 */
        public static String URI_DOMESTIC_REFUNDS_QUERY = "/refund/domestic/refunds/";
        /** 申请交易账单 */
        public static String URI_TRADE_BILLS = "/bill/tradebill";
        /** 申请资金账单 */
        public static String URI_FUND_FLOW_BILLS = "/bill/fundflowbill";
        public static String URI_GATEWAY = "https://api.mch.weixin.qq.com/v3";

        private Long userId;
        private Long deptId;
        private String tradeNo;
        private String subject;
        private BigDecimal totalAmount;
        private String ip;
        private String openid;
        private String type;
        private String h5Info;
        private String gateway;
        private String uri;

        public Builder() {
            this.gateway = URI_GATEWAY;
        }

        public Builder userId(Long param){
            userId = param;
            return this;
        }

        public Builder deptId(Long param){
            deptId = param;
            return this;
        }
        public Builder tradeNo(String param){
            tradeNo = param;
            return this;
        }
        public Builder subject(String param){
            subject = param;
            return this;
        }
        public Builder totalAmount(BigDecimal param){
            totalAmount = param;
            return this;
        }
        public Builder ip(String param){
            ip = param;
            return this;
        }
        public Builder openidWithH5OrJsapi(String param){
            openid = param;
            return this;
        }

        public WeChatPayInfo buildNative(){
            type = TYPE_NATIVE;
            uri = URI_PAY_TRANSACTIONS;
            return new WeChatPayInfo(this);
        }
        public WeChatPayInfo buildH5WithAndroid(){
            type = TYPE_H5;
            h5Info = H5INFO_ANDROID;
            uri = URI_PAY_TRANSACTIONS;
            return new WeChatPayInfo(this);
        }
        public WeChatPayInfo buildH5WithIOS(){
            type = TYPE_H5;
            h5Info = H5INFO_IOS;
            uri = URI_PAY_TRANSACTIONS;
            return new WeChatPayInfo(this);
        }
    }

    private WeChatPayInfo(Builder builder) {
        userId = builder.userId;
        deptId = builder.deptId;
        tradeNo = builder.tradeNo;
        subject = builder.subject;
        totalAmount = builder.totalAmount;
        ip = builder.ip;
        openid = builder.openid;
        type = builder.type;
        h5Info = builder.h5Info;
        gateway = builder.gateway;
        uri = builder.uri;
    }
}
