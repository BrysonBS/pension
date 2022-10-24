package com.ruoyi.pension.tuya.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.websocket.WebSocketUsers;
import com.ruoyi.pension.tuya.domain.MqttConnectInfo;
import com.ruoyi.pension.tuya.domain.message.TuyaMqttMessage;
import lombok.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TuyaMqttClient implements MqttCallback, IMqttActionListener,Comparable<TuyaMqttClient> {
    private static final Logger log = LoggerFactory.getLogger(TuyaMqttClient.class);
    private static final ConcurrentHashMap.KeySetView<Node, Boolean> SET = ConcurrentHashMap.newKeySet();
    private MqttAsyncClient client;
    private TuyaMqttClientManager tuyaMqttClientManager;
    private MqttConnectInfo mqttConnectInfo;
    private String dataDirectory;
    private IMqttToken token;
    public ObjectMapper objectMapper;
    private String uid;

    public TuyaMqttClient(String uid) {
        this.tuyaMqttClientManager = SpringUtils.getBean(TuyaMqttClientManager.class);
        this.objectMapper = SpringUtils.getBean(ObjectMapper.class);
        this.dataDirectory = SpringUtils.getBean(Environment.class).getProperty("tuya.mqtt.data-directory");
        this.uid = uid;
        this.restart();
        //System.out.println("=====new TuyaMqttClient===");
    }
    public synchronized MqttConnectInfo getMqttConnectInfo(){
        return this.mqttConnectInfo;
    }
    public synchronized void restart(){
        //不存在 OR 快过期则重新获取
        if(mqttConnectInfo == null || (mqttConnectInfo.getCreateTimeSeconds() + mqttConnectInfo.getExpireTime() - 300 < Instant.now().getEpochSecond())){
            this.mqttConnectInfo = this.tuyaMqttClientManager.getMqttConnectInfo(this.uid);
            MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(this.dataDirectory);
            try {
                this.client = new MqttAsyncClient(mqttConnectInfo.getUrl(),mqttConnectInfo.getClientId(),dataStore);
            } catch (MqttException e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
            client.setCallback(this);
            //连接
            this.connect();
            //默认订阅
            this.subscribe(mqttConnectInfo.getSourceTopicIpc(),0);
        }
        //否则重新连接
        else this.connect();
    }
    public void connect(){
        if(client.isConnected()) return;

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(this.mqttConnectInfo.getUsername());
        options.setPassword(this.mqttConnectInfo.getPassword().toCharArray());
        options.setConnectionTimeout(10);
        //options.setKeepAliveInterval(keepalive);
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        //options.setServerURIs(new String[]{mqttConfig.getUrl()});

        try {
            token = client.connect(options, null,this);
        } catch (MqttException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }
    public void disconnect(){
        if(client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }
    }
    public boolean isConnect(){ return client.isConnected(); }
    public void publish(TuyaMqttMessage tuyaMqttMessage, String topic) {
        //第一次添加
        if("configs".equals(tuyaMqttMessage.getType())) {
            //添加订阅对象
            SET.add(new Node(tuyaMqttMessage.getUser().getUserId() + "", tuyaMqttMessage.getOperate()));
            return;
        }
        //断开连接后10秒删除
        if("disconnect".equals(tuyaMqttMessage.getType())){
            //10秒后移除
            CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS)
                    .execute(() -> {
                        SET.remove(new Node(tuyaMqttMessage.getUser().getUserId() + "", tuyaMqttMessage.getOperate()));
                        //System.out.println("====remove: "+ tuyaMqttMessage.getUser().getUserId());
                    });
        }

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(objectMapper.writeValueAsString(tuyaMqttMessage.getMqttPayload()).getBytes());
            message.setQos(0);
            message.setRetained(false);
            IMqttDeliveryToken pubToken;
            synchronized (this) {
                try {
                    pubToken = client.publish(topic, message);
                    pubToken.waitForCompletion(1000L);
                } catch (MqttException e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }
    public void subscribe(String topicName, int qos){
        try {
            token.waitForCompletion();
            IMqttToken subToken = client.subscribe(topicName, qos);
            subToken.waitForCompletion();
        } catch (MqttException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }
    public void cleanTopic(String topic) {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe(topic);
            } catch (MqttException e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }
        else log.error("取消订阅失败: mqtt客户端不存在或断开连接！");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message){
        //收到消息后发送到指定用户集
        SET.forEach(node ->
            WebSocketUsers.sendMessageToUsers(node.getUserId() + "", AjaxResult.success()
                    .put(AjaxResult.OPERATE_TAG, node.getOperate())
                    .put(AjaxResult.DATA_TAG, message.toString())
            )
        );
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        System.out.println("mqtt连接成功: " + mqttConnectInfo.getLinkId());
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        System.out.println("mqtt连接失败: " + exception.getMessage());
        log.error("mqtt连接失败: {}", exception.getMessage());
    }

    public void connectionLost(Throwable cause) {
        System.out.println("mqtt连接断开: " + cause.getMessage());
        log.error("mqtt连接断开: {}", cause.getMessage());
    }

    public int users(){
        return SET.size();
    }

    @Override
    public int compareTo(TuyaMqttClient o) {
        return this.users() - o.users();
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    class Node{
        private String userId;
        private String operate;
    }
}
