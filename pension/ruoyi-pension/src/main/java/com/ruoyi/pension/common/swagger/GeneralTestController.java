package com.ruoyi.pension.common.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.common.api.*;
import com.ruoyi.framework.websocket.WebSocketUsers;
import com.ruoyi.pension.common.domain.bo.AlipayInfo;
import com.ruoyi.pension.common.domain.bo.WeChatPayInfo;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.po.PensionPaymentNotifyWechat;
import com.ruoyi.pension.common.domain.vo.NoticeVo;
import com.ruoyi.pension.common.service.PensionPaymentService;
import com.ruoyi.pension.nursing.domain.po.NursingOrder;
import com.ruoyi.pension.tuya.service.TuyaDeviceService;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.artemis.api.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Tag(name = "一般接口Api")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/swagger/general")
public class GeneralTestController extends BaseController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OssManager ossManager;
    @Autowired
    private PensionPaymentService pensionPaymentService;
    @Autowired
    private AlipayManager alipayManager;
    @Autowired
    private WeChatPayManager weChatPayManager;
    @Autowired
    private OrderNumberManager orderNumberManager;
    @Autowired
    private Environment environment;
    @Qualifier("jmsTemplateDelay")
    @Autowired
    private JmsTemplate jmsTemplate;

    @Qualifier("jmsTemplateTime")
    @Autowired
    private JmsTemplate jmsTemplateTime;
    @Autowired
    private TuyaDeviceService tuyaDeviceService;
    private final AlimsManager alimsManager;


    @Operation(summary = "tuya设备列表",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/tuya/deviceList")
    public AjaxResult tuyaDeviceList(String uid){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,tuyaDeviceService.getAllDeviceList(uid));
    }
    @Operation(summary = "关闭应用",security = { @SecurityRequirement(name = "Authorization") })
    @Parameter(description = "Content-Type", required = true,
            style = ParameterStyle.SIMPLE,
            schema = @Schema(implementation = String.class),
            name = "Content-Type", in = ParameterIn.HEADER,
            example = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/system/shutdown")
    public void shutdown(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            request.getRequestDispatcher("/monitor/shutdown").forward(request,response);
    }


    @Operation(summary = "websocket测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/wstest")
    public AjaxResult websocketTest(String userId) throws JsonProcessingException {
        NoticeVo noticeVo = NoticeVo.builder()
                .tags(List.of("标签1","标签2"))
                .info("[测试][测试][测试][心率:10]")
                .time(LocalDateTime.now())
                .name("测试设备")
                .build();
        WebSocketUsers.sendMessageToUsers(userId,AjaxResult.success()
                .put(AjaxResult.OPERATE_TAG,PensionBusiness.NOTIFICATION)
                .put(AjaxResult.DATA_TAG,noticeVo));
        return AjaxResult.success();
    }

    @Operation(summary = "发送短信测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/sendSms")
    public AjaxResult SendSmsText(String name,String phone) throws Exception {
        alimsManager.sendAlertSms(name,List.of(phone));
        return AjaxResult.success();
    }
    @Operation(summary = "发送语音测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/sendVms")
    public AjaxResult SendVmsText(String name,String phone) throws Exception {
        alimsManager.sendAlertVms(name,phone);
        return AjaxResult.success();
    }


    @Operation(summary = "文件上传到OSS测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/uploadByStream")
    public AjaxResult uploadByStream(){
        String target = "nursing/record/abc.txt";
        InputStream input = new ByteArrayInputStream("abcd23451".getBytes());
        return ossManager.uploadByStream(target,input) ? AjaxResult.success() : AjaxResult.error();
    }
    @Operation(summary = "当前所属商户测试",security = { @SecurityRequirement(name = "Authorization") })
    @Parameter(name = "deptId",description = "部门id",schema = @Schema(name = "Long",implementation = Long.class))
    @GetMapping("/pensionPayment")
    public AjaxResult pensionPayment(Long deptId,Integer payType){
        return AjaxResult.success()
                  .put(AjaxResult.DATA_TAG,
                          pensionPaymentService.getOneOrAncestorByDeptIdAndPayType(deptId,payType));
    }
    @Operation(summary = "支付宝二维码支付",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/alipayQR")
    public void alipayQR(HttpServletResponse response) throws IOException {
        BigDecimal totalAmount = BigDecimal.valueOf(1.02D);
        String orderNumber = orderNumberManager.getAlipayOrderNumber(getDeptId());
        AlipayInfo alipayInfo = AlipayInfo.builder()
                .userId(getUserId())
                .deptId(getDeptId())
                .totalAmount(totalAmount)
                .tradeNo(orderNumber)
                .subject("斐爱科技")
                .build();
        ObjectNode objectNode = alipayManager.paymentPreCreate(alipayInfo);

        //禁止图片缓存
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //根据图片类型来修改相应的contentType
        response.setContentType("image/png");

        //自定义响应头,添加金额和订单号信息
        response.addHeader("pay-amount",objectNode.get("totalAmount").asText());
        response.addHeader("pay-order",objectNode.get("outTradeNo").asText());
        QRCodeUtil.createCodeAndLogoToOutputStream(objectNode.get("qr_code").asText(), "E:/wechat.png", response.getOutputStream());
    }
    @Operation(summary = "支付宝退款测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/alipay/refund")
    public void alipayRefund(Long deptId,Long userId,String outTradeNo,BigDecimal refund){
        alipayManager.refundOutTradeNo(deptId,userId,outTradeNo,refund);
    }
    @Operation(summary = "微信退款测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/weChatPay/refund")
    public AjaxResult weChatPayRefund(Long deptId,Long userId,String outTradeNo,BigDecimal total,BigDecimal refund) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        return toAjax(weChatPayManager.refundOutTradeNo(deptId,userId,outTradeNo,total,refund));
    }
    @Operation(summary = "微信支付测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/wxpay")
    public void weChatPay(HttpServletResponse response) throws Exception {
        BigDecimal totalAmount = BigDecimal.valueOf(0.01D);
        String orderNumber = orderNumberManager.getWeChatPayOrderNumber(getDeptId());
        WeChatPayInfo info = WeChatPayInfo.builder()
                .ip(getLoginUser().getIpaddr())
                .userId(getUserId())
                .deptId(getDeptId())
                .totalAmount(totalAmount)
                .tradeNo(orderNumber)
                .subject("斐爱科技")
                .buildNative();
        //获取支付code_url
        String code = objectMapper.readTree(weChatPayManager.paymentPay(info))
                .get("code_url").asText();

        //禁止图片缓存
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //根据图片类型来修改相应的contentType
        response.setContentType("image/png");

        //自定义响应头,添加金额和订单号信息
        response.addHeader("pay-amount",totalAmount.stripTrailingZeros().toPlainString());
        response.addHeader("pay-order",orderNumber);
        QRCodeUtil.createCodeAndLogoToOutputStream(code, "E:/wechat.png", response.getOutputStream());

    }

    @Operation(summary = "微信支付回调测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/weChatPay/notify")
    public void notifyTest() throws IOException {
        String notify = """
                {"decryptData":"{\\"mchid\\":\\"1628460381\\",\\"appid\\":\\"wxf21e085c26149e84\\",\\"out_trade_no\\":\\"PO120020220725000000036\\",\\"transaction_id\\":\\"4200001541202207256544048265\\",\\"trade_type\\":\\"NATIVE\\",\\"trade_state\\":\\"SUCCESS\\",\\"trade_state_desc\\":\\"支付成功\\",\\"bank_type\\":\\"OTHERS\\",\\"attach\\":\\"102\\",\\"success_time\\":\\"2022-07-25T13:35:02+08:00\\",\\"payer\\":{\\"openid\\":\\"ozZkx0XNqucER5kGN7953Pnfdw_c\\"},\\"amount\\":{\\"total\\":1,\\"payer_total\\":1,\\"currency\\":\\"CNY\\",\\"payer_currency\\":\\"CNY\\"}}","id":"477b63fb-eb02-50fd-b0aa-9a8adbb434be","create_time":"2022-07-25T13:35:02+08:00","event_type":"TRANSACTION.SUCCESS","resource_type":"encrypt-resource","summary":"支付成功","resource":{"algorithm":"AEAD_AES_256_GCM","ciphertext":"v5zOffMea0VS69pSKC/SdFq1+KTySsAVgdvWeLy2PlDN/aLD5cd7WG5lQ1WmO0hPhJqqF6Hmf9C7MW29/DKpFNWqV67IeVdH7vR3jnLcTiEsgqZ81juDZiDxV4Xtl6DoFKBn0TGMVP0KBhxhj4CFvUGPoYV/MVtdKCmIEhjA4pQWdCGLZacqxG3QuNmUBBFrbyFeX+uJBJYC67dRDGIqvlB77yLRnsG6uxFO9dI3B6QCPZ6H18Js6WhzxD/OcEQFta/fCVJm2xLNzb1J9OUqqYfXLRyjLcVah7qL6AVQ/V/X+a+rZIJjlNoNbmOzNK92qJ9pGruQM3DxjyLpsAR6iwkKCM3475lYA7uiUKDCYdg57XU9LLnf+uAECnnAtucCX9h65Ak2GZ6ze5THT1WxlMfhoTbgfOwYjtye9XH9RglmZerRZBnuQZ8rustca2G0qiaa0UlCBRMs9F1cRny+KPddk1ck8x/Rdi3F0YlcrtckAJRiopLUoQ20h0Gl6/4G9qjxhMX6GxjuR9lIu04nd61B8PEieMKWKnUR7xdB7JFYKw2P7uqAVIQOFW2LMwTDSOg8kAXY8F4pPw==","associated_data":"transaction","nonce":"4PICSNuyx8wt","original_type":"transaction"}} 
                """;
        Notification notification = objectMapper.readValue(notify,Notification.class);
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
        NoticeVo noticeVo = NoticeVo.builder()
                .type(PensionBusiness.PENSION_PAYMENT)
                .name("微信支付")
                .info("支付成功!")
                .build();
        WebSocketUsers.sendMessageToUsers(notifyWechat.getAttach(),AjaxResult.success()
                .put(AjaxResult.OPERATE_TAG,PensionBusiness.PENSION_PAYMENT)
                .put(AjaxResult.DATA_TAG,noticeVo));
    }

    @Operation(summary = "延迟消息队列测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/message/delay")
    public void delayMessageTest(){
        //发送自动关闭延迟消息
        jmsTemplate.convertAndSend(Objects.requireNonNull(environment.getProperty("message-queue.nursing-order-close")),
                1
                , message -> {
            message.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), Instant.now().plus(5, ChronoUnit.SECONDS).toEpochMilli());
            return message;
        });
        //发送自动完成延迟消息
        jmsTemplate.convertAndSend(Objects.requireNonNull(environment.getProperty("message-queue.nursing-order-complete")),
                2
                , message -> {
            message.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), Instant.now().plus(10, ChronoUnit.SECONDS).toEpochMilli());
            return message;
        });
    }
    @Operation(summary = "有期限的消息队列-发送",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/message/time/send")
    public void timeMessageSendTest(){
        //发送自动完成延迟消息
        jmsTemplateTime.convertAndSend("pension.time.queue",
                NursingOrder.builder()
                        .orderSn("22222222")
                        .payType(2)
                        .build());
    }
    @Operation(summary = "有期限的消息队列-接收",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/message/time/receive")
    public String timeMessageReceive(){
        NursingOrder nursingOrder = (NursingOrder) jmsTemplateTime.receiveAndConvert("pension.time.queue");
        System.out.println(nursingOrder);
        return nursingOrder == null ? "null" : nursingOrder.toString();
    }
}
