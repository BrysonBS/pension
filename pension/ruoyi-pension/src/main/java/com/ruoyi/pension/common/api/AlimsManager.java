package com.ruoyi.pension.common.api;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dyvmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.pension.common.domain.po.LogSms;
import com.ruoyi.pension.common.service.LogSmsService;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AlimsManager {
    @Value("${aliyun.accessKey}")
    private String accessKey;
    @Value("${aliyun.secret}")
    private String secret;
    private final ObjectMapper objectMapper;
    private final LogSmsService logSmsService;
    private StaticCredentialProvider provider;
    @Autowired
    public void setStaticCredentialProvider() {
        this.provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKey)
                .accessKeySecret(secret)
                .build());
    }

    private final ExecutorService executorService = Executors.newWorkStealingPool();

    /** ============  短信通知相关   ============== */
    /**
     * 设备报警通知
     * @param name 设备名
     * @param phones 电话号列表
     */
    public void sendAlertSms(String name,List<String> phones){
        sendSms(Template.SMS_ALERT,
                objectMapper.createObjectNode().put("name",name),
                phones);
    }
    /**
     * 登录确认验证码
     * @param code
     * @param phone
     */
    public void sendLoginSms(String code,String phone){
        sendCodeSms(Template.SMS_CODE_LOGIN_CONFIRM,code,phone);
    }
    /**
     * 用户注册验证码
     * @param code
     * @param phone
     */
    public void sendRegisterSms(String code,String phone){
        sendCodeSms(Template.SMS_CODE_REGISTER,code,phone);
    }
    /**
     * 修改密码验证码
     * @param code
     * @param phone
     */
    public void sendModifyPasswordSms(String code,String phone){
        sendCodeSms(Template.SMS_CODE_MODIFY_PASSWORD,code,phone);
    }
    public void sendCodeSms(AlimsManager.Template template,String code,String phone){
        sendSms(template,
                objectMapper.createObjectNode().put("code",code),
                List.of(phone));
    }
    public void sendSms(AlimsManager.Template template, ObjectNode objectNode, List<String> phones){
        if(phones == null || phones.isEmpty()) return;
        try (com.aliyun.sdk.service.dysmsapi20170525.AsyncClient client = com.aliyun.sdk.service.dysmsapi20170525.AsyncClient.builder()
                .region("cn-qingdao") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                                .setConnectTimeout(Duration.ofSeconds(5))
                )
                .build()) {
            SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                    .signName("斐爱科技")
                    .templateCode(template.getCode())
                    .phoneNumbers(String.join(",", phones))
                    .templateParam(objectNode.toString())
                    .build();
            client.sendSms(sendSmsRequest)
                    .thenAcceptAsync(response -> {
                        //发送后保存记录
                        LogSms.LogSmsBuilder builder = LogSms.builder()
                                .content(objectNode.toString())
                                .phones(String.join(",", phones))
                                .sendTime(LocalDateTime.now())
                                .templateCode(template.getCode());
                        Optional.of(response)
                                .flatMap(resp -> Optional.ofNullable(resp.getBody()))
                                .ifPresent(body -> builder
                                        .resultMessage(body.getMessage())
                                        .requestId(body.getRequestId())
                                        .resultCode(body.getCode())
                                        .resultBizid(body.getBizId()));
                        logSmsService.save(builder.build());
                    },executorService)
                    .join();
        }
    }

    /** ============  语音通知相关   ============== */
    /**
     * 设备报警通知
     * @param name 设备名
     * @param phone 电话号
     */
    public void sendAlertVms(String name, String phone){
        sendVms(Template.VMS_PUBLIC_ALERT,
                objectMapper.createObjectNode().put("name",name),
                phone);
    }
    /**
     * 登录确认验证码
     * @param code
     * @param phone
     */
    public void sendLoginVms(String code,String phone){
        sendCodeVms(Template.VMS_PERSONAL_CODE_LOGIN_CONFIRM,code,phone);
    }
    /**
     * 用户注册验证码
     * @param code
     * @param phone
     */
    public void sendRegisterVms(String code,String phone){
        sendCodeVms(Template.VMS_PERSONAL_CODE_REGISTER,code,phone);
    }
    /**
     * 修改密码验证码
     * @param code
     * @param phone
     */
    public void sendModifyPasswordVms(String code,String phone){
        sendCodeVms(Template.VMS_PERSONAL_CODE_MODIFY_PASSWORD,code,phone);
    }
    public void sendCodeVms(AlimsManager.Template template,String code,String phone){
        sendVms(template,
                objectMapper.createObjectNode()
                        .put("product","斐爱科技")
                        .put("code",code),
                phone);
    }
    public void sendVms(AlimsManager.Template template, ObjectNode objectNode, String phone){
        if(phone == null) return;
        try (AsyncClient client = AsyncClient.builder()
                .region("cn-qingdao") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dyvmsapi.aliyuncs.com")
                                .setConnectTimeout(Duration.ofSeconds(5))
                )
                .build()) {
            SingleCallByTtsRequest singleCallByTtsRequest = SingleCallByTtsRequest.builder()
                    .calledNumber(phone)
                    .ttsCode(template.getCode())
                    .ttsParam(objectNode.toString())
                    .playTimes(1)
                    .volume(100)
                    .speed(0)
                    .build();
            client.singleCallByTts(singleCallByTtsRequest)
                    .thenAcceptAsync(singleCallByTtsResponse -> {
                        //发送后保存记录
                        LogSms.LogSmsBuilder builder = LogSms.builder()
                                .content(objectNode.toString())
                                .phones(phone)
                                .sendTime(LocalDateTime.now())
                                .templateCode(template.getCode());
                        Optional.of(singleCallByTtsResponse)
                                .flatMap(resp -> Optional.ofNullable(resp.getBody()))
                                .ifPresent(body -> builder
                                        .resultMessage(body.getMessage())
                                        .requestId(body.getRequestId())
                                        .resultCode(body.getCode())
                                        .resultBizid(body.getCallId()));
                        logSmsService.save(builder.build());
                    },executorService)
                    .join();
        }
    }
    @AllArgsConstructor
    @Getter
    enum Template{
        SMS_ALERT("设备报警","SMS_217406265"),
        SMS_CODE_IDENTITY("身份验证验证码赠","SMS_176950030"),
        SMS_CODE_LOGIN_CONFIRM("登录确认验证码赠","SMS_176950029"),
        SMS_CODE_LOGIN_ERROR("登录异常验证码赠","SMS_176950028"),
        SMS_CODE_REGISTER("用户注册验证码赠","SMS_176950027"),
        SMS_CODE_MODIFY_PASSWORD("修改密码验证码赠","SMS_176950026"),
        SMS_CODE_MODIFY_INFO("信息变更验证码赠","SMS_176950025"),

        VMS_PUBLIC_ALERT("报警通知","TTS_211498369"),
        VMS_PERSONAL_CODE_IDENTITY("身份验证验证码","TTS_211845304"),
        VMS_PERSONAL_CODE_LOGIN_CONFIRM("登录确认验证码","TTS_211845303"),
        VMS_PERSONAL_CODE_LOGIN_ERROR("登录异常验证码","TTS_211845302"),
        VMS_PERSONAL_CODE_REGISTER("用户注册验证码","TTS_211845301"),
        VMS_PERSONAL_CODE_CONFIRM("活动确认验证码","TTS_211845300"),
        VMS_PERSONAL_CODE_MODIFY_PASSWORD("修改密码验证码","TTS_211845299"),
        VMS_PERSONAL_CODE_MODIFY_INFO("信息变更验证码","TTS_211845298");

        final String description;
        final String code;
    }
}
