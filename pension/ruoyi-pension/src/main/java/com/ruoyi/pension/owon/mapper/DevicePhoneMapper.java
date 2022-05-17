package com.ruoyi.pension.owon.mapper;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.owon.domain.po.DevicePhone;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【owon_device_phone】的数据库操作Mapper
* @createDate 2022-04-24 11:05:59
* @Entity com.ruoyi.pension.owon.domain.po.DevicePhone
*/
public interface DevicePhoneMapper extends BaseMapper<DevicePhone> {
    List<DevicePhone> getByDeviceIdAndSource(@Param("deviceId") Integer deviceId,@Param("source") Integer source);
    int removeByDeviceIdsAndSource(@Param("idList") Collection<? extends Serializable> idList,@Param("source") Integer source);
}




