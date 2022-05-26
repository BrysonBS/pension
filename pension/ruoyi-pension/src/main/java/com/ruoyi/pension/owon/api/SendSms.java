package com.ruoyi.pension.owon.api;

import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.pension.owon.domain.po.LogSms;
import com.ruoyi.pension.owon.service.LogSmsService;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.*;

public class SendSms {
    public static void sendWarning(String name,String[] phones) throws Exception {
        if(name == null || phones == null || phones.length == 0) return;

        String templateCode = "SMS_217406265";
        String strPhones = Arrays.stream(phones)
                .reduce((p1,p2) -> p1+","+p2).get();

        Environment environment = SpringUtils.getBean(Environment.class);
        String accessKey = environment.getProperty("aliyun.accessKey");
        String secret = environment.getProperty("aliyun.secret");

        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKey)
                // 您的AccessKey Secret
                .setAccessKeySecret(secret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("斐爱科技")
                .setTemplateCode(templateCode)
                .setTemplateParam("{\"name\":\""+name+"\"}")
                .setPhoneNumbers(strPhones);//"13939929193,15737970915"
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse resp = client.sendSms(sendSmsRequest);
                    //发送后保存记录
        LogSms logSms = LogSms.builder()
                    .content(name)
                    .phones(strPhones)
                    .sendTime(LocalDateTime.now())
                    .templateCode(templateCode)
                    .build();
            //保存发送记录
            SendSmsResponseBody body = resp.getBody();
            if(body != null) {
                logSms.setRequestId(body.getRequestId());
                logSms.setResultMessage(body.getMessage());
                logSms.setResultBizid(body.getBizId());
                logSms.setResultCode(body.getCode());
            }
            LogSmsService logSmsService = SpringUtils.getBean(LogSmsService.class);
            logSmsService.save(logSms);
    }
}
