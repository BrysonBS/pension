package com.ruoyi.pension.common.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.pension.nursing.domain.po.NursingOrder;
import com.ruoyi.pension.owon.domain.dto.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

import javax.jms.ConnectionFactory;
import java.util.Map;

@Configuration
public class MessageConfig {

    @Bean
    public JmsTemplate jmsTemplateDelay(ConnectionFactory connectionFactory){
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setExplicitQosEnabled(false);
        template.setDeliveryPersistent(true);
        return template;
    }
    @Bean
    public JmsTemplate jmsTemplateTime(ConnectionFactory connectionFactory){
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setExplicitQosEnabled(true);
        template.setTimeToLive(2000);//消息存活时间
        template.setReceiveTimeout(1500);//接收消息最大等待时间
        return template;
    }
    @Bean
    public JmsTemplate jmsTemplateCommon(ConnectionFactory connectionFactory){
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setExplicitQosEnabled(true);
        template.setTimeToLive(10000);//消息存活时间
        return template;
    }

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
