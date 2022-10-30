package com.ruoyi.pension.common.message;

import com.ruoyi.common.core.domain.model.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Component
@Slf4j
public class PensionMessageServer {
    @Qualifier("jmsTemplateCommon")
    private final JmsTemplate jmsTemplate;
    private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;
    private final JmsListenerContainerFactory<?> jmsListenerContainerFactory;
    private final MappingJackson2MessageConverter messageConverter;

    private final ConcurrentHashMap<String,Set<Long>> TOPICS = new ConcurrentHashMap<>();

    public void publish(PensionMessage pensionMessage){

    }
    public void publish(@Valid PensionMessage pensionMessage, String topic){
        Set<Long> userIdSet = TOPICS.getOrDefault(topic,ConcurrentHashMap.newKeySet());
        userIdSet.add(pensionMessage.getUser().getUserId());
        jmsTemplate.convertAndSend(topic,pensionMessage);
    }
    public void subscribe(String topic,LoginUser user, Consumer<PensionMessage> consumer){
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId(topic);
        endpoint.setDestination(topic);
        endpoint.setMessageListener(message -> {
            try {
                if(messageConverter.fromMessage(message) instanceof PensionMessage pensionMessage){
                    consumer.accept(pensionMessage);
                }
            } catch (JMSException e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        });
        jmsListenerEndpointRegistry.registerListenerContainer(endpoint,jmsListenerContainerFactory,true);
    }
    public void unsubscribe(String topic,LoginUser user){
        Optional.ofNullable(jmsListenerEndpointRegistry.getListenerContainer(topic))
                .ifPresent(MessageListenerContainer::stop);
    }
}
