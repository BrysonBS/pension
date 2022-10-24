package com.ruoyi.pension.tuya.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TuyaSecondsDeserializer extends StdDeserializer<LocalDateTime> {
    public TuyaSecondsDeserializer() {
        this(null);
    }
    protected TuyaSecondsDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        long seconds = p.getValueAsLong();
        return LocalDateTime.ofEpochSecond(seconds,0, ZoneOffset.of("+08:00"));
    }
}
