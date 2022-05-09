package com.ruoyi.pension.owon.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.websocket.WebSocketUsers;
import com.ruoyi.pension.owon.api.SendSms;
import com.ruoyi.pension.owon.domain.vo.NoticeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Api("ws")
@RestController
public class WsTestController extends BaseController {
    @Autowired
    private ObjectMapper objectMapper;
    @ApiOperation("websocket测试")
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

    @ApiOperation("发送短信测试")
    @GetMapping("/sendSms")
    public AjaxResult SendSmsText(String phone) throws Exception {
        SendSms.sendWarning("Sensor-D11396",new String[]{phone});
        return AjaxResult.success();
    }
}
