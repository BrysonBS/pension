package com.ruoyi.pension.tuya.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ruoyi.pension.tuya.domain.TuyaDevice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TuyaDeviceDeserializer extends StdDeserializer<TuyaDevice> {
    public TuyaDeviceDeserializer() {
        super(TuyaDevice.class);
    }
    @Override
    public TuyaDevice deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode body = p.readValueAsTree();
        String timeZone = body.at("/time_zone").asText();
        return TuyaDevice.builder()
                .category(body.at("/category").asText())
                .categoryName(body.at("/category_name").asText())
                .createTime(LocalDateTime.ofEpochSecond(body.at("/create_time").asLong(),0,ZoneOffset.of(timeZone)))
                .icon(body.at("/icon").asText())
                .ip(body.at("/ip").asText())
                .lat(body.at("/lat").asText())
                .localKey(body.at("/local_key").asText())
                .lon(body.at("/lon").asText())
                .model(body.at("/model").asText())
                .name(body.at("/name").asText())
                .online(body.at("/online").asBoolean())
                .ownerId(body.at("/owner_id").asText())
                .productId(body.at("/product_id").asText())
                .sub(body.at("/sub").asBoolean())
                .deviceId(body.at("/id").asText())
                .timeZone(timeZone)
                .build();
    }
}
