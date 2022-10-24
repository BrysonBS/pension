package com.ruoyi.pension.tuya.mapper;

import com.ruoyi.pension.tuya.domain.TuyaDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【tuya_device】的数据库操作Mapper
* @createDate 2022-10-17 17:54:09
* @Entity com.ruoyi.pension.tuya.domain.TuyaDevice
*/
public interface TuyaDeviceMapper extends BaseMapper<TuyaDevice> {
    List<TuyaDevice> listByExample(TuyaDevice tuyaDevice);
}




