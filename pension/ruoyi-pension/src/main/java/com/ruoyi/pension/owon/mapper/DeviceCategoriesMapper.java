package com.ruoyi.pension.owon.mapper;

import com.ruoyi.pension.owon.domain.po.DeviceCategories;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_device_categories】的数据库操作Mapper
* @createDate 2022-04-27 14:17:57
* @Entity com.ruoyi.pension.owon.domain.po.DeviceCategories
*/
public interface DeviceCategoriesMapper extends BaseMapper<DeviceCategories> {
    List<DeviceCategories> getAllList();
}




