package com.ruoyi.pension.common.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.common.config.AlipayConfig;
import com.ruoyi.pension.common.config.WeChatPayConfig;
import com.ruoyi.pension.common.domain.bo.AlipayInfo;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.po.PensionPayment;
import com.ruoyi.pension.common.domain.po.PensionRefundAlipay;
import com.ruoyi.pension.common.service.BaseOrderService;
import com.ruoyi.pension.common.service.PensionPaymentService;
import com.ruoyi.pension.common.service.PensionRefundAlipayService;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class AlipayManager {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment environment;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private OrderNumberManager orderNumberManager;
    @Autowired
    private PensionPaymentService pensionPaymentService;
    @Autowired
    private SysDeptOwonService sysDeptOwonService;
    @Autowired
    private PensionRefundAlipayService pensionRefundAlipayService;
    @Autowired
    private List<BaseOrderService> baseOrderServiceList;

    public AlipayConfig getAlipayConfig(Long deptId){
        //先尝试从缓存获取
        AlipayConfig config = redisCache.getCacheObject(PensionBusiness.getKeyOfAliConfig(deptId));
        if(config != null) return config;
        //否则从数据库查找
        PensionPayment pensionPayment = pensionPaymentService.getOneOrAncestorByDeptIdAndPayType(deptId,1);
        if(pensionPayment == null) throw new RuntimeException("支付宝商户没有配置!");
        if(StringUtils.isBlank(pensionPayment.getNotifyUrl()))
            pensionPayment.setNotifyUrl(environment.getProperty("alipay.notifyUrl") + deptId);
        if(StringUtils.isBlank(pensionPayment.getRefundNotifyUrl()))
            pensionPayment.setRefundNotifyUrl(environment.getProperty("alipay.returnUrl"));
        config = AlipayConfig.builder()
                .appId(pensionPayment.getAppId())
                .appPrivateKey(pensionPayment.getAppPrivateKey())
                .certModel(pensionPayment.getCertModel())
                .gateway(environment.getProperty("alipay.gateway"))
                .signType(environment.getProperty("alipay.signType"))
                .notifyUrl(environment.getProperty("alipay.notifyUrl"))
                .returnUrl(environment.getProperty("alipay.returnUrl"))
                .timeout(5)
                .timeUnit(TimeUnit.MINUTES)
                .deptName(pensionPayment.getDeptName())
                .build();
        if(config.getCertModel()){ //证书模式
            config.setAlipayCertificatePath(RuoYiConfig.getUploadPath());
            config.setAlipayCertPublicKeyPath(pensionPayment.getAlipayCertPath());
            config.setAlipayRootCertPath(pensionPayment.getAlipayRootCertPath());
            config.setAppCertPublicKeyPath(pensionPayment.getAppCertPath());
            config.setAlipayCertPublicKeyContent(getContent(config.getAlipayCertificatePath() + config.getAlipayCertPublicKeyPath()));
            config.setAlipayRootCertContent(getContent(config.getAlipayCertificatePath() + config.getAlipayRootCertPath()));
            config.setAppCertPublicKeyContent(getContent(config.getAlipayCertificatePath() + config.getAppCertPublicKeyPath()));
        }
        else  config.setAlipayPublicKey(pensionPayment.getAlipayPublicKey());
        //先放入缓存
        redisCache.setCacheObject(PensionBusiness.getKeyOfAliConfig(deptId),config,24, TimeUnit.HOURS);
        return config;
    }
    public AlipayClient getAlipayClient(AlipayConfig alipayConfig) throws AlipayApiException {
        AlipayClient alipayClient;
        //否则直接创建
        if(!alipayConfig.getCertModel()){//公钥模式
            alipayClient =  new DefaultAlipayClient(alipayConfig.getGateway(),
                    alipayConfig.getAppId(),
                    alipayConfig.getAppPrivateKey(),
                    "json", "UTF-8",
                    alipayConfig.getAlipayPublicKey(), "RSA2");
        }
        else { //证书模式
            CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
            certAlipayRequest.setServerUrl(alipayConfig.getGateway());
            certAlipayRequest.setAppId(alipayConfig.getAppId());//应用私钥
            certAlipayRequest.setPrivateKey(alipayConfig.getAppPrivateKey());
            certAlipayRequest.setFormat("json");
            certAlipayRequest.setCharset("UTF-8");
            certAlipayRequest.setSignType(alipayConfig.getSignType());
            //应用公钥证书
            certAlipayRequest.setCertContent(alipayConfig.getAppCertPublicKeyContent());
            certAlipayRequest.setAlipayPublicCertContent(alipayConfig.getAlipayCertPublicKeyContent());
            certAlipayRequest.setRootCertContent(alipayConfig.getAlipayRootCertContent());
            alipayClient = new DefaultAlipayClient(certAlipayRequest);
        }
        return alipayClient;
    }
    /** 当面付,直接生成二维码连接返回 */
    public ObjectNode paymentPreCreate(AlipayInfo alipayInfo){
        AlipayConfig alipayConfig = getAlipayConfig(alipayInfo.getDeptId());
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl() + alipayInfo.getDeptId());
        String subject = alipayInfo.getSubject() == null ? alipayConfig.getDeptName() : alipayInfo.getSubject();
        ObjectNode bizContentNode = objectMapper.createObjectNode()
                .put("out_trade_no", alipayInfo.getTradeNo())
                .put("total_amount", alipayInfo.getTotalAmount().stripTrailingZeros().toPlainString())
                .put("body",alipayInfo.getUserId())
                .put("subject", subject);

        request.setBizContent(bizContentNode.toString());

        AlipayTradePrecreateResponse response = null;
        try {
            AlipayClient alipayClient = getAlipayClient(alipayConfig);
            response = alipayConfig.getCertModel() ?
                    alipayClient.certificateExecute(request) :
                    alipayClient.execute(request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        if(!response.isSuccess()) {
            log.error(response.getMsg(),response);
            throw new RuntimeException("失败: " + response.getMsg());
        }

        return objectMapper.createObjectNode()
                .put("outTradeNo",response.getOutTradeNo())
                .put("qr_code",response.getQrCode())
                .put("totalAmount",alipayInfo.getTotalAmount());
    }

    /** 根据订单号退款 */
    public boolean refundOutTradeNo(Long deptId,Long userId,String outTradeNo,BigDecimal refund){
        AlipayConfig alipayConfig = getAlipayConfig(deptId);
        try {
            String refundNo = orderNumberManager.getRefundOrderNumber(outTradeNo);
            AlipayClient alipayClient = getAlipayClient(alipayConfig);
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizContent(objectMapper.createObjectNode()
                    .put("out_trade_no",outTradeNo)
                    .put("refund_amount",refund.stripTrailingZeros().toPlainString())
                    .put("out_request_no",refundNo)
                    .toString()
            );
            AlipayTradeRefundResponse response = alipayConfig.getCertModel() ?
                    alipayClient.certificateExecute(request) :
                    alipayClient.execute(request);
            //持久化
            PensionRefundAlipay refundAlipay = PensionRefundAlipay.builder()
                    .userId(userId)
                    .deptId(deptId)
                    .refundNo(refundNo)
                    .outTradeNo(outTradeNo)
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .buyerLogonId(response.getBuyerLogonId())
                    .buyerUserId(response.getBuyerUserId())
                    .gmtRefundPay(response.getGmtRefundPay() == null ? null : response.getGmtRefundPay().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .refundFeeReal(new BigDecimal(response.getSendBackFee()))
                    .refundFeeTotal(new BigDecimal(response.getRefundFee()))
                    .success(response.isSuccess())
                    .build();
            pensionRefundAlipayService.save(refundAlipay);
            //先更新订单退款总金额
            if(response.isSuccess())
                baseOrderServiceList.forEach(e -> e.updateOrderRefundAmountByOrderNo(refundAlipay.getOutTradeNo(), refundAlipay.getRefundFeeReal()));
            return response.isSuccess();
        } catch (AlipayApiException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }

    //清除缓存(含微信)
    public void refreshCache(Long deptId){
        redisCache.deleteObject(sysDeptOwonService
                .getListDeptAndChildrenByDeptId(deptId)
                .stream()
                .flatMap(id -> Stream.of(PensionBusiness.getKeyOfAliConfig(id),
                        PensionBusiness.getKeyOfWeChatConfig(id)))
                .collect(Collectors.toList()));
    }

    private String getContent(String absolutePath){
        try(FileInputStream inputStream = new FileInputStream(absolutePath)){
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException("证书文件不存在");
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException("证书读取失败");
        }
    }
}
