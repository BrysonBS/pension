package com.ruoyi.pension.common.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.AntCertificationUtil;
import com.alipay.api.internal.util.codec.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.websocket.WebSocketUsers;
import com.ruoyi.pension.common.api.AlipayManager;
import com.ruoyi.pension.common.api.WeChatPayManager;
import com.ruoyi.pension.common.config.AlipayConfig;
import com.ruoyi.pension.common.config.WeChatPayConfig;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.po.PensionPaymentNotify;
import com.ruoyi.pension.common.domain.po.PensionPaymentNotifyWechat;
import com.ruoyi.pension.common.domain.po.PensionRefundWechat;
import com.ruoyi.pension.common.domain.vo.NoticeVo;
import com.ruoyi.pension.common.service.BaseOrderService;
import com.ruoyi.pension.common.service.PensionPaymentNotifyService;
import com.ruoyi.pension.common.service.PensionPaymentNotifyWechatService;
import com.ruoyi.pension.common.service.PensionRefundWechatService;
import com.ruoyi.pension.nursing.service.NursingOrderService;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.constant.WechatPayHttpHeaders;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.exception.ParseException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/callback")
public class PensionPaymentCallbackController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WeChatPayManager weChatPayManager;
    @Autowired
    private AlipayManager alipayManager;
    @Autowired
    private PensionPaymentNotifyService pensionPaymentNotifyService;
/*    @Autowired
    private NursingOrderService nursingOrderService;*/
    @Autowired
    private List<BaseOrderService> baseOrderServiceList;
    @Autowired
    private PensionPaymentNotifyWechatService pensionPaymentNotifyWechatService;
    @Autowired
    private PensionRefundWechatService pensionRefundWechatService;

    @PostMapping("/notify/alipay/{deptId}")  // 注意这里必须是POST接口
    public String payNotifyOfAlipay(@PathVariable("deptId") Long deptId,HttpServletRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }
        AlipayConfig alipayConfig = alipayManager.getAlipayConfig(deptId);
        String alipayPublicKey;
        if(alipayConfig.getCertModel()){ //证书模式
            X509Certificate cert = AntCertificationUtil.getCertFromContent(alipayConfig.getAlipayCertPublicKeyContent());
            PublicKey publicKey = cert.getPublicKey();
            alipayPublicKey = Base64.encodeBase64String(publicKey.getEncoded());
        }
        else alipayPublicKey = alipayConfig.getAlipayPublicKey();

        //先进行支付宝验签: 新版SDK无需移除sign、sign_type
        boolean signVerifyNotify = AlipaySignature.verifyV1(params,alipayPublicKey,"UTF-8",alipayConfig.getSignType());
        //失败则直接返回
        if(!signVerifyNotify) return "failure";

        //验签成功则保存数据
        PensionPaymentNotify pensionPaymentNotify = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .build()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,true)
                .convertValue(requestParams,PensionPaymentNotify.class);

        if(pensionPaymentNotify.getReceiptAmount() != null) {//支付回调
            boolean success = "TRADE_SUCCESS".equalsIgnoreCase(pensionPaymentNotify.getTradeStatus());
            String outTradeNo = pensionPaymentNotify.getOutTradeNo();
            //先通知用户
            NoticeVo noticeVo = NoticeVo.builder()
                    .type(PensionBusiness.PENSION_PAYMENT)
                    .payType("1")//1:支付宝,2:微信
                    .outTradeNo(outTradeNo)
                    .tradeState(pensionPaymentNotify.getTradeStatus())
                    .info(success ? "支付成功" : "支付失败")
                    .build();

            //支付成功则更新订单状态
            if(success) baseOrderServiceList.forEach(e -> e.updateOrderStatusToPaidByOrderNo(outTradeNo));

            WebSocketUsers.sendMessageToUsers(params.get("body"),
                    AjaxResult.success()
                            .put(AjaxResult.OPERATE_TAG,PensionBusiness.PENSION_PAYMENT)
                            .put(AjaxResult.DATA_TAG,noticeVo));
        }

        //保存到数据库
        pensionPaymentNotifyService.saveAndCache(pensionPaymentNotify);
        return "success";
    }
    @RequestMapping("/notify/weChatPay/{deptId}")
    public String payNotifyOfWeChatPay(@PathVariable("deptId") Long deptId,HttpServletRequest request) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException, ValidationException, ParseException {
        //解析
        Notification notification = parseWeChatPayToNotification(deptId,request);
        //存储
        return responseToWeChatPay(saveWeChatPayNotification(notification));
    }

    @RequestMapping("/refund/weChatPay/{deptId}")
    public String payRefundOfWeChatPay(@PathVariable("deptId") Long deptId,HttpServletRequest request) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException, ValidationException, ParseException {
        Notification notification = parseWeChatPayToNotification(deptId,request);
        return  responseToWeChatPay(saveWeChatPayRefundNotification(notification));
    }

    private Notification parseWeChatPayToNotification(Long deptId,HttpServletRequest request) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException, ValidationException, ParseException {
        WeChatPayConfig weChatPayConfig = weChatPayManager.getWeChatPayConfig(deptId);
        //获取验签器
        Verifier verifier = weChatPayManager.getVerifier(weChatPayConfig);
        //构建request,传入必要参数
        NotificationRequest notificationRequest = new NotificationRequest.Builder()
                //平台证书序列号
                .withSerialNumber(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_SERIAL))
                //请求头Wechatpay-Nonce
                .withNonce(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_NONCE))
                //请求头Wechatpay-Timestamp
                .withTimestamp(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_TIMESTAMP))
                //请求头Wechatpay-Signature
                .withSignature(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_SIGNATURE))
                //请求体
                .withBody(getWeChatNotifyBody(request))
                .build();
        NotificationHandler handler = new NotificationHandler(verifier,weChatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        //验签和解析请求体
        return handler.parse(notificationRequest);
    }
    private boolean saveWeChatPayRefundNotification(Notification notification) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(notification.getDecryptData());
        PensionRefundWechat refundWechat = PensionRefundWechat.builder()
                .refundId(jsonNode.at("/refund_id").asText())
                .outTradeNo(jsonNode.at("/out_trade_no").asText())
                .transactionId(jsonNode.at("/transaction_id").asText())
                .outRefundNo(jsonNode.at("/out_refund_no").asText())
                .refundStatus(jsonNode.at("/refund_status").asText())
                .successTime(LocalDateTime.parse(jsonNode.at("/success_time").asText(),DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .total(jsonNode.at("/amount/total").asLong())
                .refund(jsonNode.at("/amount/refund").asLong())
                .payerTotal(jsonNode.at("/amount/payer_total").asLong())
                .payerRefund(jsonNode.at("/amount/payer_refund").asLong())
                .userReceivedAccount(jsonNode.at("/user_received_account").asText())
                .build();
        //先更新订单退款金额
        if("SUCCESS".equalsIgnoreCase(refundWechat.getRefundStatus()))
            baseOrderServiceList.forEach(e -> e.updateOrderRefundAmountByOrderNo(refundWechat.getOutTradeNo(),
                    BigDecimal.valueOf(refundWechat.getPayerRefund())
                            .divide(BigDecimal.valueOf(100L),2,RoundingMode.HALF_UP)));
        //再持久化:幂等操作
        return pensionRefundWechatService.saveIfAbsent(refundWechat);
    }
    private boolean saveWeChatPayNotification(Notification notification) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(notification.getDecryptData());
        PensionPaymentNotifyWechat notifyWechat = PensionPaymentNotifyWechat.builder()
                .notifyId(notification.getId())
                .createTime(LocalDateTime.parse(notification.getCreateTime(),DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .eventType(notification.getEventType())
                .resourceType(notification.getResourceType())
                .mchid(jsonNode.at("/mchid").asText())
                .appid(jsonNode.at("/appid").asText())
                .outTradeNo(jsonNode.at("/out_trade_no").asText())
                .transactionId(jsonNode.at("/transaction_id").asText())
                .tradeType(jsonNode.at("/trade_type").asText())
                .tradeState(jsonNode.at("/trade_state").asText())
                .tradeStateDesc(jsonNode.at("/trade_state_desc").asText())
                .bankType(jsonNode.at("/bank_type").asText())
                .attach(jsonNode.at("/attach").asText())
                .successTime(LocalDateTime.parse(jsonNode.at("/success_time").asText(),DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .openid(jsonNode.at("/payer/openid").asText())
                .total(jsonNode.at("/amount/total").asLong())
                .payerTotal(jsonNode.at("/amount/payer_total").asLong())
                .currency(jsonNode.at("/amount/currency").asText())
                .payerCurrency(jsonNode.at("/amount/payer_currency").asText())
                .build();
        //先通知用户
        String outTradeNo = notifyWechat.getOutTradeNo();
        String tradeState = notifyWechat.getTradeState();
        NoticeVo noticeVo = NoticeVo.builder()
                .type(PensionBusiness.PENSION_PAYMENT)
                .payType("2")//1:支付宝,2:微信
                .outTradeNo(outTradeNo)
                .tradeType(notifyWechat.getTradeType())
                .tradeState(tradeState)
                .info(notifyWechat.getTradeStateDesc())
                .build();
        //更新订单状态为已支付
        if("SUCCESS".equalsIgnoreCase(tradeState))
            baseOrderServiceList.forEach(e -> e.updateOrderStatusToPaidByOrderNo(outTradeNo));
        WebSocketUsers.sendMessageToUsers(notifyWechat.getAttach(),
                AjaxResult.success()
                        .put(AjaxResult.OPERATE_TAG,PensionBusiness.PENSION_PAYMENT)
                        .put(AjaxResult.DATA_TAG,noticeVo));
        //存储:保证幂等性
        return pensionPaymentNotifyWechatService.saveIfAbsent(notifyWechat);
    }
    private String getWeChatNotifyBody(HttpServletRequest request) throws IOException {
        //将通知参数转为字符串
        StringBuilder builder = new StringBuilder();
        try(BufferedReader reader = request.getReader()){
            String line;
            while ((line = reader.readLine()) != null){
                if(builder.length() > 0 ) builder.append("\n");
                builder.append(line);
            }
        }
        return builder.toString();
    }
    private String responseToWeChatPay(boolean success){
        //正常执行结束并存储成功则回调应答成功
        return success ? objectMapper.createObjectNode()
                .put("code","SUCCESS")
                .put("message","成功")
                .toString() :
        //否则回调应答失败
        objectMapper.createObjectNode()
                .put("code","error")
                .put("message","失败")
                .toString();
    }
}
