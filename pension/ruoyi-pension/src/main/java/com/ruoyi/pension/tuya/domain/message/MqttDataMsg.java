package com.ruoyi.pension.tuya.domain.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MqttDataMsg {
    private MqttDataMsg() {}
    // 默认: webrtc
    private String mode;
    // 浏览器生成的offer
    private String sdp;
    // 码流类型，默认为1
    @JsonProperty("stream_type")
    private Integer streamType;
    // webRTC认证需要的授权码，从开放平台获取
    private String auth;
    //候选地址，a=candidate:1922393870 1 UDP 2130706431 192.168.1.171 51532 typ host
    private String candidate;

    public static OfferBuilder offerBuilder(){
        return new OfferBuilder();
    }
    public static CandidateBuilder candidateBuilder(){
        return new CandidateBuilder();
    }
    public static AnswerBuilder answerBuilder(){
        return new AnswerBuilder();
    }
    public static DisconnectBuilder disconnectBuilder(){
        return new DisconnectBuilder();
    }
    public static class OfferBuilder {
        private String mode = "webrtc";
        private String sdp;
        private Integer streamType = 1;
        private String auth;
        public OfferBuilder mode(String value){
            mode = value;
            return this;
        }
        public OfferBuilder sdp(String value){
            sdp = value;
            return this;
        }
        public OfferBuilder streamType(Integer value){
            streamType = value;
            return this;
        }
        public OfferBuilder auth(String value){
            auth = value;
            return this;
        }
        public MqttDataMsg build(){
            MqttDataMsg msg = new MqttDataMsg();
            msg.setMode(this.mode);
            msg.setSdp(this.sdp);
            msg.setStreamType(this.streamType);
            msg.setAuth(this.auth);
            return msg;
        }
    }
    public static class CandidateBuilder{
        private String mode = "webrtc";
        private String candidate;
        public CandidateBuilder mode(String value){
            mode = value;
            return this;
        }
        public CandidateBuilder candidate(String value){
            candidate = value;
            return this;
        }
        public MqttDataMsg build(){
            MqttDataMsg msg = new MqttDataMsg();
            msg.setMode(this.mode);
            msg.setCandidate(this.candidate);
            return msg;
        }
    }
    public static class AnswerBuilder{
        private String mode = "webrtc";
        private String sdp;
        public AnswerBuilder mode(String value){
            mode = value;
            return this;
        }
        public AnswerBuilder sdp(String value){
            sdp = value;
            return this;
        }
        public MqttDataMsg build(){
            MqttDataMsg msg = new MqttDataMsg();
            msg.setMode(this.mode);
            msg.setSdp(this.sdp);
            return msg;
        }
    }
    public static class DisconnectBuilder{
        private String mode = "webrtc";
        public DisconnectBuilder mode(String value){
            mode = value;
            return this;
        }
        public MqttDataMsg build(){
            MqttDataMsg msg = new MqttDataMsg();
            msg.setMode(this.mode);
            return msg;
        }
    }
    }
