package com.ruoyi.pension.owon.convertor;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Data;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class OwonMillsDeserializer extends StdDeserializer<LocalDateTime> {
    public OwonMillsDeserializer() {
        this(null);
    }
    protected OwonMillsDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String text = jsonParser.getText();
        if(!text.chars().allMatch(Character::isDigit)){ //"yyyy-MM-dd HH:mm:ss"处理
            return LocalDateTime.parse(text,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return ZonedDateTime
                .of(2000,1,1,0,0,0,0,ZoneId.of("UTC"))//UTC0
                .plusSeconds(Long.parseLong(jsonParser.getText()))
                .plusHours(8)//UTC+8
                .toLocalDateTime();
    }
}
