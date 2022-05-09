package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.owon.domain.po.DevicePhone;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.DevicePhoneMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_device_phone】的数据库操作Service
* @createDate 2022-04-24 11:05:59
*/
@Service
public class DevicePhoneService extends ServiceImpl<DevicePhoneMapper, DevicePhone> implements IService<DevicePhone> {
    public List<DevicePhone> getByDeviceId(Integer id){
        return this.baseMapper.getByDeviceId(id);
    }

    public boolean removeByDeviceIds(Collection<? extends Serializable> idList){
        this.baseMapper.removeByDeviceIds(idList);
        return true;
    }
}
