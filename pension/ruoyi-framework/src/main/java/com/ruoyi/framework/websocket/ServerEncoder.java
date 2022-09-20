package com.ruoyi.framework.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ServerEncoder implements Encoder.Text<AjaxResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEncoder.class);

    @Override
    public String encode(AjaxResult object) throws EncodeException {
        ObjectMapper objectMapper = SpringUtils.getBean(ObjectMapper.class);
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.info("\n[websocket消息转换字符串失败]",e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {

    }
}
