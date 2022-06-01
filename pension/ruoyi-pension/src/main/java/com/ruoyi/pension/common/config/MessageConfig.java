package com.ruoyi.pension.common.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.pension.owon.domain.dto.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

import java.util.Map;

@Configuration
public class MessageConfig {
    //类为:org.springframework.jms.support.converter.MappingJackson2MessageConverter

    //消息转换器
    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .build()
        );

        /** 默认: 使用完整类名作为标记 **/
        //messageConverter.setTypeIdPropertyName("_typeId");
        //输出: ActiveMQMessage[null]:PERSISTENT/ClientMessageImpl[messageID=0, durable=true, address=null,userID=null,properties=TypedProperties[_typeId=cn.tacos.tacocloud.domain.jpa.PopInStock]]

        /** 重新命名_typeId的值(仅对指定的类型更改) **/
        messageConverter.setTypeIdPropertyName("_typeId");
        messageConverter.setTypeIdMappings(Map.of("response", Response.class));
        //输出: ActiveMQMessage[null]:PERSISTENT/ClientMessageImpl[messageID=0, durable=true, address=null,userID=null,properties=TypedProperties[_typeId=popInStock]]
        return messageConverter;
    }
}
