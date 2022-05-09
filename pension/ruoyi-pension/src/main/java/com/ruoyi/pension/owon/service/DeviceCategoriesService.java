package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.owon.domain.po.DeviceCategories;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.DeviceCategoriesMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_device_categories】的数据库操作Service
* @createDate 2022-04-27 14:17:57
*/
@Service
public class DeviceCategoriesService extends ServiceImpl<DeviceCategoriesMapper, DeviceCategories> implements IService<DeviceCategories> {
    public List<DeviceCategories> getIdAndNameList() {
        return this.baseMapper.getAllList();
    }
}
