package com.ruoyi.pension.owon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

@Configuration
public class OwonConfig {
    //维护一个单例jackson对象
    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .build();
    }
    @Bean(name = "cacheMap") //简易缓存,用于线程间通信
    public ConcurrentHashMap<String,SynchronousQueue<String>> concurrentHashMap(){
        return new ConcurrentHashMap<>();
    }
}
