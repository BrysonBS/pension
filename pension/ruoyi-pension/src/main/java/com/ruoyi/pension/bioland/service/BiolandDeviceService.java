package com.ruoyi.pension.bioland.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.bioland.domain.po.BiolandDevice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.bioland.domain.po.BiolandDeviceCategories;
import com.ruoyi.pension.bioland.mapper.BiolandDeviceMapper;
import com.ruoyi.pension.owon.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.DevicePhone;
import com.ruoyi.pension.owon.service.DevicePhoneService;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
* @author Administrator
* @description 针对表【bioland_device】的数据库操作Service
* @createDate 2022-05-14 13:42:09
*/
@Service
public class BiolandDeviceService extends ServiceImpl<BiolandDeviceMapper, BiolandDevice> implements IService<BiolandDevice> {
    @Autowired
    @Qualifier("BiolandDeviceCategoriesService")
    private DeviceCategoriesService deviceCategoriesService;
    @Autowired
    private DevicePhoneService devicePhoneService;
    public List<BiolandDevice> getListByDeptIdsAndDevice(Collection<Long> deptIds, BiolandDevice device) {
        if(deptIds.isEmpty()) deptIds = null;
        return this.baseMapper.getListByDeptIdsAndDevice(deptIds,device);
    }
    public List<String> getPhonesBySerialNumber(String serialNumber){
        return this.baseMapper.getPhonesBySerialNumber(serialNumber);
    }

    @Transactional
    public void saveOrUpdateBySerialNumber(BiolandDevice device){
        this.baseMapper.saveOrUpdateBySerialNumber(device);
    }
    @Transactional
    public boolean updateNameAndDeptIdAndPhonesById(BiolandDevice device){
        //更新设备所属部门和名称
        LambdaUpdateWrapper<BiolandDevice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(device.getName() != null,BiolandDevice::getName,device.getName())
                .set(device.getDeptId() != null,BiolandDevice::getDeptId,device.getDeptId())
                .set(device.getCategoriesId() != null,BiolandDevice::getCategoriesId,device.getCategoriesId())
                .eq(BiolandDevice::getId,device.getId());
        super.update(updateWrapper);
        //更新绑定手机号
        DevicePhone[] phones = device.getPhones();
        //设置电话归属平台
        for(DevicePhone p : phones) p.setSource(Platform.BIOLAND);
        Integer[] phonesId = device.getPhonesId();
        if(phonesId != null && phonesId.length > 0) //删除
            devicePhoneService.removeByIds(List.of(phonesId));
        if(phones!= null && phones.length > 0) //更新
            devicePhoneService.saveOrUpdateBatch(List.of(phones));
        return true;
    }
    @Transactional
    public void saveIfNotPresent(BiolandDevice device){
        BiolandDevice biolandDevice = this.selectOneBySerialNumber(device.getSerialNumber());
        if(biolandDevice == null) {
            if(!Strings.isBlank(device.getDeviceType())){
                BiolandDeviceCategories biolandDeviceCategories = deviceCategoriesService.getOneByDeviceType(device.getDeviceType());
                if(biolandDeviceCategories != null)
                    device.setCategoriesId(biolandDeviceCategories.getId());
            }
            super.save(device);
        }
    }
    @Transactional
    public BiolandDevice  selectOneBySerialNumber(String serialNumber){
        return this.baseMapper.selectOneBySerialNumber(serialNumber);
    }

    @Transactional
    public boolean deleteDeviceAndPhoneByIds(List<Integer> ids){
        return removeByIds(ids) && devicePhoneService.removeByDeviceIdsAndSource(ids, Platform.BIOLAND);
    }
}
