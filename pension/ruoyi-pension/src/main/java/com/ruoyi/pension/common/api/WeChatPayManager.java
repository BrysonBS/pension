package com.ruoyi.pension.common.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.common.config.WeChatPayConfig;
import com.ruoyi.pension.common.domain.bo.WeChatPayInfo;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.po.PensionPayment;
import com.ruoyi.pension.common.domain.po.PensionRefundWechat;
import com.ruoyi.pension.common.service.PensionPaymentService;
import com.ruoyi.pension.common.service.PensionRefundWechatService;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WeChatPayManager {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment environment;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private PensionPaymentService pensionPaymentService;
    @Autowired
    private OrderNumberManager orderNumberManager;
    @Autowired
    private PensionRefundWechatService pensionRefundWechatService;

    /** 获取商户配置文件 */
    public WeChatPayConfig getWeChatPayConfig(Long deptId){
        //先尝试从缓存获取
        WeChatPayConfig config = redisCache.getCacheObject(PensionBusiness.getKeyOfWeChatConfig(deptId));
        if(config != null) return config;
        //否则从数据库查找
        PensionPayment pensionPayment = pensionPaymentService.getOneOrAncestorByDeptIdAndPayType(deptId,2);
        if(pensionPayment == null) throw new RuntimeException("微信商户没有配置!");
        if(StringUtils.isBlank(pensionPayment.getNotifyUrl()))
            pensionPayment.setNotifyUrl(environment.getProperty("wechatpay.notifyUrl") + deptId);
        if(StringUtils.isBlank(pensionPayment.getRefundNotifyUrl()))
            pensionPayment.setRefundNotifyUrl(environment.getProperty("wechatpay.refundNotifyUrl"));
        config = WeChatPayConfig.builder()
                .merchantId(pensionPayment.getMerchantId())
                .wechatPayCertificatePath(RuoYiConfig.getUploadPath())
                .privateKeyPath(pensionPayment.getWechatpayKeyPemPath())
                .merchantSerialNumber(pensionPayment.getMerchantSerialNumber())
                .apiV3Key(pensionPayment.getApiV3Key())
                .appId(pensionPayment.getAppId())
                .gateway(environment.getProperty("wechatpay.gateway"))
                .notifyUrl(pensionPayment.getNotifyUrl())
                .refundNotifyUrl(pensionPayment.getRefundNotifyUrl())
                .deptName(pensionPayment.getDeptName())
                .build();
        //读取证书内容
        try(FileInputStream inputStream = new FileInputStream(config.getWechatPayCertificatePath() + config.getPrivateKeyPath())){
            config.setPrivateKeyContent(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException("证书不存在");
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException("证书读取失败");
        }
        //先放入缓存
        redisCache.setCacheObject(PensionBusiness.getKeyOfWeChatConfig(deptId),config,24, TimeUnit.HOURS);
        return config;
    }
    /** 获取证书管理器实例 */
    public Verifier getVerifier(WeChatPayConfig weChatPayConfig) throws IOException, GeneralSecurityException, HttpCodeException, NotFoundException {
        //商户私钥
        PrivateKey privateKey = PemUtil.loadPrivateKey(weChatPayConfig.getPrivateKeyContent());
        //私钥签名对象
        PrivateKeySigner privateKeySigner = new PrivateKeySigner(weChatPayConfig.getMerchantSerialNumber(),privateKey);
        //身份认证对象
        WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(weChatPayConfig.getMerchantId(),privateKeySigner);
        //使用定时的签名验证器,不需要传入证书
        CertificatesManager certificatesManager = CertificatesManager.getInstance();
        certificatesManager.putMerchant(weChatPayConfig.getMerchantId(),
                wechatPay2Credentials,
                weChatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        return certificatesManager.getVerifier(weChatPayConfig.getMerchantId());
    }
    /** 获取支付HTTP请求对象 */
    public CloseableHttpClient getWeChatPayClient(WeChatPayConfig weChatPayConfig) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        PrivateKey privateKey = PemUtil.loadPrivateKey(weChatPayConfig.getPrivateKeyContent());
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(weChatPayConfig.getMerchantId(), weChatPayConfig.getMerchantSerialNumber(), privateKey)
                //验签
                .withValidator(new WechatPay2Validator(getVerifier(weChatPayConfig)));
        return builder.build();
    }
    /** 获取Http请求对象: 无需进行应答签名验证,跳过验签流程 */
    public CloseableHttpClient getWeChatPayNoSignClient(WeChatPayConfig weChatPayConfig) throws IOException {
        PrivateKey privateKey = PemUtil.loadPrivateKey(weChatPayConfig.getPrivateKeyContent());
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(weChatPayConfig.getMerchantId(), weChatPayConfig.getMerchantSerialNumber(), privateKey)
                .withValidator(response -> true);
        return builder.build();
    }
    /** 统一支付 */
    public String paymentPay(WeChatPayInfo payInfo) throws IOException, GeneralSecurityException, NotFoundException, HttpCodeException {
        WeChatPayConfig weChatPayConfig = getWeChatPayConfig(payInfo.getDeptId());
        String subject = payInfo.getSubject() == null ? weChatPayConfig.getDeptName() : payInfo.getSubject();
        /** 请求参数设置 */
        ObjectNode paramsNode = objectMapper.createObjectNode()
            .put("appid",weChatPayConfig.getAppId())
            .put("mchid",weChatPayConfig.getMerchantId())
            .put("description",subject)
            .put("out_trade_no",payInfo.getTradeNo())
            .put("time_expire", ZonedDateTime.now()
                    .plusMinutes(5)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssxxx")))
            .put("notify_url",weChatPayConfig.getNotifyUrl())
            .put("attach",payInfo.getUserId()+"");
        //金额信息
        paramsNode.putObject("amount")
                .put("total", payInfo.getTotalAmount().multiply(BigDecimal.valueOf(100L)).longValue())
                //金额单位为分
                .put("currency","CNY");
        //场景信息
        ObjectNode sceneNode = paramsNode.putObject("scene_info")
                .put("payer_client_ip",payInfo.getIp());

        //H5与JSAPI需要添加额外参数
        String type = payInfo.getType();
        if("h5".equals(type))
            sceneNode.putObject("h5_info")
                            .put("type",payInfo.getH5Info());
        else if("jsapi".equals(type) || "sub_jsapi".equals(type))
            paramsNode.putObject("payer")
                            .put("openid",payInfo.getOpenid());
        String newType = type.replace("sub_","");

        //发送请求
        String body = weChatHttpPost(payInfo.getGateway() + payInfo.getUri() + newType,
                paramsNode.toString(),getWeChatPayClient(weChatPayConfig));

        // 设置签名信息,Native与H5不需要,直接返回code_url
        if("h5".equals(type) || "native".equals(type)) return body;
        //非Native与h5则通过prepayId生成签名返回
        return payWithSign(objectMapper.readTree(body).get("prepay_id").asText(),type,weChatPayConfig)
                .toString();
    }
    /** 根据订单号查询 */
    public String queryByOutTradeNo(Long deptId,String outTradeNo) throws IOException, GeneralSecurityException, NotFoundException, HttpCodeException {
        WeChatPayConfig weChatPayConfig = getWeChatPayConfig(deptId);
        String url = WeChatPayInfo.Builder.URI_GATEWAY
                .concat(WeChatPayInfo.Builder.URI_ORDER_QUERY_BY_NO)
                .concat(outTradeNo)
                .concat("?mchid=").concat(weChatPayConfig.getMerchantId());
        return weChatHttpGet(url,getWeChatPayClient(weChatPayConfig));
    }
    /** 关闭订单 */
    public void closeOutTradeNo(Long deptId,String outTradeNo) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        WeChatPayConfig weChatPayConfig = getWeChatPayConfig(deptId);
        String url = WeChatPayInfo.Builder.URI_GATEWAY.concat(
                String.format(WeChatPayInfo.Builder.URI_CLOSE_ORDER_BY_NO,outTradeNo));
        weChatHttpPost(url,
                objectMapper.createObjectNode()
                        .put("mchid",weChatPayConfig.getMerchantId())
                        .toString(),
                getWeChatPayClient(weChatPayConfig));
    }
    /**
     * 退款
     * @param deptId
     * @param outTradeNo 订单号
     * @param total 订单金额
     * @param refund 退款金额
     */
    public boolean refundOutTradeNo(Long deptId,Long userId,String outTradeNo,BigDecimal total,BigDecimal refund) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        WeChatPayConfig weChatPayConfig = getWeChatPayConfig(deptId);
        String url = WeChatPayInfo.Builder.URI_GATEWAY.concat(WeChatPayInfo.Builder.URI_DOMESTIC_REFUNDS);
        //参数设置
        String params = objectMapper.createObjectNode()
                .put("out_trade_no",outTradeNo)
                .put("out_refund_no",orderNumberManager.getRefundOrderNumber(outTradeNo))
                .put("reason","申请退款") // 退款原因
                .put("notify_url", weChatPayConfig.getRefundNotifyUrl() + "/" + deptId)
                .set("amount",objectMapper.createObjectNode()
                        .put("refund",refund.multiply(BigDecimal.valueOf(100)).longValue())//单位:分
                        .put("total",total.multiply(BigDecimal.valueOf(100)).longValue())//单位:分
                        .put("currency","CNY")
                ).toString();
        //先保存退款记录
        JsonNode jsonNode = objectMapper.readTree(weChatHttpPost(url,params,getWeChatPayClient(weChatPayConfig)));
        String refundStatus = jsonNode.at("/status").asText();
        pensionRefundWechatService.save(PensionRefundWechat.builder()
                .deptId(deptId)
                .userId(userId)
                .channel(jsonNode.at("/channel").asText())
                .createTime(LocalDateTime.parse(jsonNode.at("/create_time").asText(),DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .outRefundNo(jsonNode.at("/out_refund_no").asText())
                .outTradeNo(jsonNode.at("/out_trade_no").asText())
                .refundId(jsonNode.at("/refund_id").asText())
                .refundStatus(refundStatus)
                .transactionId(jsonNode.at("/transaction_id").asText())
                .userReceivedAccount(jsonNode.at("/user_received_account").asText())
                .build()
        );
        return "PROCESSING".equalsIgnoreCase(refundStatus) || "SUCCESS".equalsIgnoreCase(refundStatus);
    }
    /** 查询单笔退款信息 */
    public String queryByOutRefundNo(Long deptId,String outRefundNo) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        WeChatPayConfig weChatPayConfig = getWeChatPayConfig(deptId);
        String url = WeChatPayInfo.Builder.URI_GATEWAY
                .concat(WeChatPayInfo.Builder.URI_DOMESTIC_REFUNDS_QUERY)
                .concat(outRefundNo);
        return weChatHttpGet(url,getWeChatPayClient(weChatPayConfig));
    }
    /**
     * 申请交易账单
     * @param billDate 仅支持三个月内的账单下载申请 ，如果传入日期未为当天则会出错
     * @param billType 分为：ALL、SUCCESS、REFUND
     * ALL：返回当日所有订单信息（不含充值退款订单）
     * SUCCESS：返回当日成功支付的订单（不含充值退款订单）
     * REFUND：返回当日退款订单（不含充值退款订单）
     * @return 下载连接
     */
    public String tradeBillToDownloadUrl(Long deptId, LocalDate billDate, String billType) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        String url = WeChatPayInfo.Builder.URI_GATEWAY
                .concat(WeChatPayInfo.Builder.URI_TRADE_BILLS)
                .concat("?bill_date=").concat(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(billDate))
                .concat("&bill_type=").concat(billType);
        return objectMapper.readTree(weChatHttpGet(url,getWeChatPayClient(getWeChatPayConfig(deptId))))
                .get("download_url")
                .asText();
    }
    /**
     *
     * @param billDate 格式yyyy-MM-dd 仅支持三个月内的账单下载申请，如果传入日期未为当天则会出错
     * @param accountType 分为：BASIC、OPERATION、FEES
     * BASIC：基本账户
     * OPERATION：运营账户
     * FEES：手续费账户
     * @return 下载连接
     */
    public String FundFlowBillToDownloadUrl(Long deptId,LocalDate billDate,String accountType) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        String url = WeChatPayInfo.Builder.URI_GATEWAY
                .concat(WeChatPayInfo.Builder.URI_FUND_FLOW_BILLS)
                .concat("?bill_date=").concat(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(billDate))
                .concat("&account_type=").concat(accountType);
        return objectMapper.readTree(weChatHttpGet(url,getWeChatPayClient(getWeChatPayConfig(deptId))))
                .get("download_url")
                .asText();
    }

    /** 网页下载 */
    public void downloadBill(Long deptId, String downloadUrl,String fileName, HttpServletResponse httpResponse) throws IOException {
        HttpGet httpGet = new HttpGet(downloadUrl);
        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse response = getWeChatPayNoSignClient(getWeChatPayConfig(deptId)).execute(httpGet);
        try(response){
            HttpEntity entity = response.getEntity();
            String charset = ContentType.getOrDefault(entity).getCharset().name();
            byte[] body = EntityUtils.toByteArray(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200 || statusCode == 204) {
                //响应设置
                httpResponse.reset();
                //响应类型
                httpResponse.setContentType("application/octet-stream; charset=" + charset);
                //文件长度
                httpResponse.setHeader("Content-Length", String.valueOf(body.length));
                //编码格式
                httpResponse.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, charset));
                httpResponse.getOutputStream()
                        .write(body);
            } else {
                String msg = "下载账单异常, 响应码 = "
                        .concat(statusCode + "")
                        .concat(", 下载账单返回结果 = ")
                        .concat(new String(body,charset));
                log.error(msg);
                throw new RuntimeException(msg);
            }
        }

    }

    //HTTP请求
    private String weChatHttpGet(String url,CloseableHttpClient closeableHttpClient) throws IOException {
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");
            //完成签名并执行请求
            try(CloseableHttpResponse response = closeableHttpClient.execute(httpGet)){
                return getResponseBody(response);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    private String weChatHttpPost(String url,String params,CloseableHttpClient closeableHttpClient) {
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(params, "utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            try(CloseableHttpResponse response = closeableHttpClient.execute(httpPost)){
                return getResponseBody(response);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            try {
                throw new RuntimeException(objectMapper.readTree(e.getMessage()
                        .substring(e.getMessage().indexOf('{'),e.getMessage().lastIndexOf('}') + 1)
                ).at("/message").asText());
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /** 获取响应体字符串 */
    private String getResponseBody(CloseableHttpResponse response) throws IOException {
        //响应体
        HttpEntity entity = response.getEntity();
        String body = entity==null?"":EntityUtils.toString(entity);
        //响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        //处理成功,204是，关闭订单时微信返回的正常状态码
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NO_CONTENT) return body;
        String msg = "微信支付请求失败,响应码 = " + statusCode + ",返回结果 = " + body;
        log.error(msg);
        throw new RuntimeException(msg);
    }

    //添加签名信息
    private ObjectNode payWithSign(String prepayId,String type,WeChatPayConfig weChatPayConfig) {
        long timeMillis = System.currentTimeMillis();
        String appId = weChatPayConfig.getAppId();
        String timeStamp = timeMillis / 1000 + "";
        String nonceStr = timeMillis + "";
        String packageStr = "prepay_id=" + prepayId;

        //公共参数
        ObjectNode resNode = objectMapper.createObjectNode()
                .put("nonceStr", nonceStr)
                .put("timeStamp", timeStamp);

        // JSAPI、SUB_JSAPI(小程序)
        if ("jsapi".equals(type) || "sub_jsapi".equals(type)) {
            resNode.put("appId", appId)
                    .put("package", packageStr)
                    // 使用字段appId、timeStamp、nonceStr、package进行签名
                    .put("paySign", generateSign(resNode, weChatPayConfig))
                    .put("signType", "HMAC-SHA256");
        }
        // APP
        if ("app".equals(type)) {
            resNode.put("appid", appId)
                    .put("prepayid", prepayId)
                    .put("package", "Sign=WXPay")
                    .put("partnerid", weChatPayConfig.getMerchantId())
                    // 使用字段appId、timeStamp、nonceStr、prepayId进行签名
                    .put("sign", generateSign(resNode, weChatPayConfig))
                    .put("signType", "HMAC-SHA256");
        }
        return resNode;
    }
    //加密数据,生成签名
    private  String generateSign(ObjectNode objectNode,WeChatPayConfig weChatPayConfig){
        try {
            List<String> signList = new ArrayList<>();
            objectNode.fields().forEachRemaining(entry -> signList.add(entry.getKey() + "=" + entry.getValue()));
            String signStr = String.join("&", signList)
                    .concat("&key=").concat(weChatPayConfig.getApiV3Key());
            Mac sha = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(weChatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha.init(secretKey);
            byte[] array = sha.doFinal(signStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            signStr = sb.toString().toUpperCase();
            return signStr;
        }catch (Exception e){
            throw new RuntimeException("加密失败！");
        }
    }


/*    //添加签名信息
    private  Map<String, Object>  paySignMsg(String prepayId,String type,WeChatPayConfig weChatPayConfig){
        long timeMillis = System.currentTimeMillis();
        String appId = weChatPayConfig.getAppId();
        String timeStamp = timeMillis/1000+"";
        String nonceStr = timeMillis+"";
        String packageStr = "prepay_id="+prepayId;

        // 公共参数
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("nonceStr",nonceStr);
        resMap.put("timeStamp",timeStamp);

        // JSAPI、SUB_JSAPI(小程序)
        if("jsapi".equals(type) || "sub_jsapi".equals(type)) {
            resMap.put("appId",appId);
            resMap.put("package", packageStr);
            // 使用字段appId、timeStamp、nonceStr、package进行签名
            String paySign = createSign(resMap,weChatPayConfig);
            resMap.put("paySign", paySign);
            resMap.put("signType", "HMAC-SHA256");
        }
        // APP
        if("app".equals(type)) {
            resMap.put("appid",appId);
            resMap.put("prepayid", prepayId);
            // 使用字段appId、timeStamp、nonceStr、prepayId进行签名
            String sign = createSign(resMap,weChatPayConfig);
            resMap.put("package", "Sign=WXPay");
            resMap.put("partnerid", weChatPayConfig.getMerchantId());
            resMap.put("sign", sign);
            resMap.put("signType", "HMAC-SHA256");
        }
        return resMap;
    }
    //加密数据
    private  String createSign(Map<String, Object> params,WeChatPayConfig weChatPayConfig){
        try {
            Map<String, Object> treeMap = new TreeMap<>(params);
            List<String> signList = new ArrayList<>(5);
            for (Map.Entry<String, Object> entry : treeMap.entrySet())
            {
                signList.add(entry.getKey() + "=" + entry.getValue());
            }
            String signStr = String.join("&", signList);

            signStr = signStr+"&key="+weChatPayConfig.getApiV3Key();

            Mac sha = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(weChatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha.init(secretKey);
            byte[] array = sha.doFinal(signStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            signStr = sb.toString().toUpperCase();
            return signStr;
        }catch (Exception e){
            throw new RuntimeException("加密失败！");
        }
    }*/
}
