package com.ruoyi.pension.bioland.mapper;
import com.ruoyi.pension.bioland.domain.po.BiolandDevice;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.bioland.domain.po.BiolandDeviceCategories;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【bioland_device_categories】的数据库操作Mapper
* @createDate 2022-05-14 11:29:44
* @Entity com.ruoyi.pension.bioland.domain.po.DeviceCategories
*/
public interface BiolandDeviceCategoriesMapper extends BaseMapper<BiolandDeviceCategories> {
    BiolandDeviceCategories getOneByDeviceType(@Param("deviceType") String deviceType);
}




