package com.ruoyi.pension.owon.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import com.ruoyi.pension.owon.domain.po.PanicButton;
import com.ruoyi.pension.owon.domain.dto.Response;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;

@Slf4j
public class OwonReportSjsonDeserializer extends StdDeserializer<Datapacket> {
    private ObjectMapper objectMapper;
    public OwonReportSjsonDeserializer() {
        this(null);
    }
    protected OwonReportSjsonDeserializer(Class<?> vc) {
        super(vc);
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }
    @Override
    public Datapacket deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String sjson = jsonParser.getText();
        JsonNode jsonNode = objectMapper.readTree(sjson);
        String type = jsonNode.at("/type").asText();
        String command = jsonNode.at("/command").asText();
        String result = jsonNode.at("/result").asText();

        log.info(System.getProperty("line.separator")+"sjson::  "+sjson);

        if(Strings.isBlank(result)){ //自动上报信息
            int rtype = jsonNode.at("/argument/rtype").asInt();
            if("record".equals(command)){ //同步安防日志数据
                if(rtype == 1){
                    Datapacket<?,?> datapacket = argVPaincButton_V(sjson,Operation.REPORT_PANIC_BUTTON);
                    datapacket.setIgnore(true);//不持久化
                    return datapacket;
                }
            }
            else if("connect".equals(command)){//心跳/同步
                Datapacket<?,?> datapacket = argVV_V(sjson,Operation.REPORT_HEART_BEAT);
                datapacket.setIgnore(true);//不持久化
                return datapacket;
            }
            else if("sensor".equals(command)){ //安防
                if("update".equals(type)){ //安防上报
                    return argVV_V(sjson,Operation.REPORT_SENSOR_UPDATE);
                }
                else if("warning".equals(type)){//安防报警
                    return v_respV(sjson,Operation.REPORT_SENSOR_WARNING);
                }
            }
            else if("batteryPercentageRemainUpdate".equals(command)){//电量上报
                return argVV_V(sjson,Operation.REPORT_BATTERY_PERCENTAGE);
            }
            else if(!jsonNode.at("/argument/epList").isEmpty()){ //EP节点列表更新上报
                return argDeviceEpV_V(sjson,Operation.REPORT_EP_LIST);
            }
            else if("bleDataReport".equals(command)){ //BLE设备上报
                if("update".equals(type)) {//BLE设备上报更新
                    return argVV_V(sjson,Operation.REPORT_BLE_DATA_UPDATE);
                }
            }
            else if("bleConnectChange".equals(command)){
                if("update".equals(type)){//BLE连接状态变化
                    return argVV_V(sjson,Operation.REPORT_BLE_CONNECT_CHANGE);
                }
            }
            else if("fallDetectNotify".equals(command)){
                if("update".equals(type)){ //跌倒报警器上报信息
                    return argVV_V(sjson,Operation.REPORT_FALL_DETECT_NOTIFY);
                }
            }
        }
        else if(Boolean.parseBoolean(result)){ //主动请求后返回成功
            int sequence = jsonNode.at("/sequence").asInt();
            if(sequence == Operation.REQUEST_EP_LIST.getCode()){
                //设备ep节点列表查询返回
                Datapacket<?,?> datapacket = v_respDeviceEp(sjson,Operation.RESULT_EP_LIST);
                datapacket.setIgnore(true);//不持久化
                return datapacket;
            }
            else if(sequence == Operation.REQUEST_STATE_ZB.getCode()){
                //设备状态查询返回
                Datapacket<?,?> datapacket = v_respV(sjson,Operation.RESULT_STATE_ZB);
                datapacket.setIgnore(true);
                return datapacket;
            }
            else if(sequence == Operation.REQUEST_GET_WARNING.getCode()){
                //报警器传感器状态查询返回
                Datapacket<?,?> datapacket = v_respV(sjson,Operation.RESULT_GET_WARNING);
                datapacket.setIgnore(true);
                return datapacket;
            }
            else if(sequence == Operation.REQUEST_BLE_LIST.getCode()){
                //BLE设备列表请求返回
                Datapacket<?,?> datapacket = v_respDeviceEp(sjson,Operation.RESULT_BLE_LIST);
                datapacket.setIgnore(true);
                return datapacket;
            }
        }
        else{ //主动请求后返回失败

        }



        //不匹配,序列化失败,仅保存json字符串
        return Datapacket.builder()
                .sjson(sjson)
                .ignore(true)//不持久化
                .build();
    }

    //datapacket
    private Datapacket<?,?> dataVV(String sjson,Operation operation) throws JsonProcessingException {
        Datapacket<Void,Void> dataPacket =
                objectMapper.readValue(sjson, new TypeReference<>(){});
        dataPacket.setOperation(operation);
        return dataPacket;
    }

    //argument
    private Datapacket<Argument<Void,Void>,Void> argVV_V(String sjson,Operation operation) throws JsonProcessingException {
        Datapacket<Argument<Void,Void>,Void> dataPacket =
                objectMapper.readValue(sjson, new TypeReference<>(){});
        dataPacket.setOperation(operation);
        return dataPacket;
    }
    private Datapacket<Argument<Void, PanicButton>, Void> argVPaincButton_V(String sjson,Operation operation) throws JsonProcessingException {
        Datapacket<Argument<Void, PanicButton>, Void> dataPacket = objectMapper.readValue(sjson, new TypeReference<Datapacket<Argument<Void, PanicButton>, Void>>() {
        });
        dataPacket.setOperation(operation);
        return dataPacket;
    }
    private Datapacket<Argument<DeviceEp,Void>,Void> argDeviceEpV_V(String sjson,Operation operation) throws JsonProcessingException {
        Datapacket<Argument<DeviceEp,Void>,Void> dataPacket =
                objectMapper.readValue(sjson, new TypeReference<>(){});
        dataPacket.setOperation(operation);
        return dataPacket;
    }

    //response
    private Datapacket<Void, Response<Void>> v_respV(String sjson,Operation operation) throws JsonProcessingException {
        Datapacket<Void, Response<Void>> dataPacket =
                objectMapper.readValue(sjson, new TypeReference<>(){});
        dataPacket.setOperation(operation);
        return dataPacket;
    }
    private Datapacket<Void,Response<DeviceEp>> v_respDeviceEp(String sjson,Operation operation) throws JsonProcessingException {
        Datapacket<Void,Response<DeviceEp>> dataPacket =
                objectMapper.readValue(sjson, new TypeReference<>(){});
        dataPacket.setOperation(operation);
        return dataPacket;
    }
}
