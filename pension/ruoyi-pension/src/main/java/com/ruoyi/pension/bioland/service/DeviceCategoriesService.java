package com.ruoyi.pension.bioland.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.bioland.domain.po.BiolandDeviceCategories;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.bioland.mapper.BiolandDeviceCategoriesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【bioland_device_categories】的数据库操作Service
* @createDate 2022-05-14 11:29:44
*/
@Service("BiolandDeviceCategoriesService")
public class DeviceCategoriesService extends ServiceImpl<BiolandDeviceCategoriesMapper, BiolandDeviceCategories> implements IService<BiolandDeviceCategories> {
    @Autowired
    private RedisCache redisCache;

    public List<BiolandDeviceCategories> getListTryCache(){
        //先尝试从缓存获取
        List<BiolandDeviceCategories> list = redisCache.getCacheObject("dict:" + BiolandDeviceCategories.class.getName());
        if(list == null){
            list = super.list();
            redisCache.setCacheObject("dict:" + BiolandDeviceCategories.class.getName(), list, 60, TimeUnit.MINUTES);
        }
        return  list;
    }
    public BiolandDeviceCategories getOneByDeviceType(String deviceType){
        return this.baseMapper.getOneByDeviceType(deviceType);
    }
}
