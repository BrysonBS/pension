package com.ruoyi.pension.tuya.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.JsonObject;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.tuya.connector.DeviceManagerConnector;
import com.ruoyi.pension.tuya.controller.TuyaDeviceController;
import com.ruoyi.pension.tuya.converter.TuyaDeviceDeserializer;
import com.ruoyi.pension.tuya.domain.TuyaDevice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.tuya.mapper.TuyaDeviceMapper;
import com.tuya.connector.api.config.Configuration;
import com.tuya.connector.api.core.DefaultConnectorFactory;
import com.tuya.connector.api.model.Result;
import com.tuya.connector.open.api.context.TuyaContextManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.utils.collections.ConcurrentHashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【tuya_device】的数据库操作Service
* @createDate 2022-10-17 17:54:09
*/
@Service
@Slf4j
public class TuyaDeviceService extends ServiceImpl<TuyaDeviceMapper, TuyaDevice> implements IService<TuyaDevice> {
    @Autowired
    private DeviceManagerConnector deviceManagerConnector;
    @Autowired
    private TuyaAppAccountService tuyaAppAccountService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 重新获取并更新到数据库
     */
    @Transactional
    public synchronized boolean refresh(){
        Set<TuyaDevice> dataSet = this.list()
                .parallelStream()
                .collect(Collectors.toSet());
        Set<TuyaDevice> cloudSet = ConcurrentHashMap.newKeySet();

        tuyaAppAccountService.list()
                .forEach(e -> cloudSet.addAll(this.getAllDeviceList(e.getUid())));
        cloudSet.removeAll(dataSet);
        return cloudSet.isEmpty() || saveBatch(cloudSet);

/*        return CompletableFuture.allOf(tuyaAppAccountService.list()
                .parallelStream()
                .map(e -> CompletableFuture.runAsync(() -> cloudSet.addAll(this.getAllDeviceList(e.getUid()))))
                .toArray(CompletableFuture[]::new))
                .thenApply(unused -> cloudSet.removeAll(dataSet))
                .thenApply(unused -> cloudSet.isEmpty() || saveBatch(cloudSet))
                .get();*/
    }
    @PensionDataScope
    public List<TuyaDevice> listByExample(TuyaDevice tuyaDevice){
        return this.baseMapper.listByExample(tuyaDevice);
    }
    public boolean updateUserIdAndDeptIdById(TuyaDevice tuyaDevice){
        if(tuyaDevice.getId() == null) return true;
        LambdaUpdateWrapper<TuyaDevice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(TuyaDevice::getUserId,tuyaDevice.getUserId())
                .set(TuyaDevice::getDeptId,tuyaDevice.getDeptId())
                .eq(TuyaDevice::getId,tuyaDevice.getId());
        return this.baseMapper.update(tuyaDevice,updateWrapper) > 0;
    }
    public List<TuyaDevice> getAllDeviceList(String uid){
        try {
            return getAllDeviceList(uid,"");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }
    public JsonObject getDeviceList(String uid, String lastRowKey){
        HashMap<String,String> map = new HashMap<>();
        map.put("source_type","tuyaUser");
        map.put("source_id",uid);
        map.put("last_row_key",lastRowKey);
        map.put("page_size","200");
        Result<JsonObject> result = deviceManagerConnector.getDeviceList(map);
        if(!result.getSuccess()){
            log.error(result.getMsg(),result);
            throw new RuntimeException(result.getMsg());
        }
        return result.getResult();
    }
    private List<TuyaDevice> getAllDeviceList(String uid,String lastRowKey) throws IOException {
        ArrayList<TuyaDevice> list = new ArrayList<>();
        boolean hasMore;
        do {
            JsonObject body = getDeviceList(uid, lastRowKey);
            list.addAll(objectMapper.copy()
                            .registerModule(new SimpleModule().addDeserializer(TuyaDevice.class,new TuyaDeviceDeserializer()))
                            .readValue(body.get("list").toString(), new TypeReference<List<TuyaDevice>>(){}));
            hasMore = body.get("has_more").getAsBoolean();
            lastRowKey = body.get("last_row_key").getAsString();
        } while (hasMore);
        //更新uid
        list.parallelStream().forEach(e -> e.setUid(uid));
        return list;
    }
}
