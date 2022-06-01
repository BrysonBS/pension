package com.ruoyi.pension.bioland.mapper;

import com.ruoyi.pension.bioland.domain.po.BiolandDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
* @author Administrator
* @description 针对表【bioland_device】的数据库操作Mapper
* @createDate 2022-05-14 13:42:09
* @Entity com.ruoyi.pension.bioland.domain.po.BiolandDevice
*/
public interface BiolandDeviceMapper extends BaseMapper<BiolandDevice> {
    List<BiolandDevice> getListByDeptIdsAndDevice(@Param("deptIds") Collection<Long> deptIds, @Param("device") BiolandDevice device);
    List<BiolandDevice> getListByExample(BiolandDevice biolandDevice);
    List<String> getPhonesBySerialNumber(@Param("serialNumber") String serialNumber);

    BiolandDevice selectOneBySerialNumber(@Param("serialNumber") String serialNumber);
    int saveOrUpdateBySerialNumber(BiolandDevice device);
}




