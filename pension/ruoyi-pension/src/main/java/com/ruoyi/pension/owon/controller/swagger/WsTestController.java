package com.ruoyi.pension.owon.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.oss.OssManager;
import com.ruoyi.framework.websocket.WebSocketUsers;
import com.ruoyi.pension.owon.api.SendSms;
import com.ruoyi.pension.owon.domain.vo.NoticeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "一般接口Api")
@RestController
@RequestMapping("/general")
public class WsTestController extends BaseController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OssManager ossManager;
    @Operation(summary = "websocket测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/wstest")
    public AjaxResult websocketTest() throws JsonProcessingException {
        NoticeVo noticeVo = NoticeVo.builder()
                .tags(List.of("标签1","标签2"))
                .info("[测试][测试][测试][心率:10]")
                .time(LocalDateTime.now())
                .name("测试设备")
                .build();
        WebSocketUsers.sendMessageToUsersByText(getLoginUser().getUserId()+"",objectMapper.writeValueAsString(noticeVo));
        return AjaxResult.success();
    }

    @Operation(summary = "发送短信测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/sendSms")
    public AjaxResult SendSmsText(String phone) throws Exception {
        SendSms.sendWarning("Sensor-D11396",new String[]{phone});
        return AjaxResult.success();
    }


    @Operation(summary = "文件上传到OSS测试",security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/uploadByStream")
    public AjaxResult uploadByStream(){
        String target = "abc.txt";
        return ossManager.uploadByStream(target) ? AjaxResult.success() : AjaxResult.error();
    }
}
