package com.ruoyi.pension.owon.mapper;

import com.ruoyi.pension.owon.domain.po.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.Collection;
import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_device】的数据库操作Mapper
* @createDate 2022-04-18 09:27:16
* @Entity com.ruoyi.pension.owon.domain.po.Device
*/
public interface DeviceMapper extends BaseMapper<Device> {
    List<Device> getListByDeptIdsAndDevice(@Param("deptIds") List<Long> deptIds,@Param("device") Device device);
    List<String> getPhonesByIeeeAndEp(@Param("ieee") String ieee,@Param("ep") Integer ep);
    List<String> selectAllByDeptIds(@Param("deptIds") List<Long> deptIds);
    List<Device> getListByDeptIdsAndCids(@Param("deptIds") List<Long> deptIds,@Param("cids") List<Integer> cids);
    Device getOneAndDeptNameByIeeeAndEp(@Param("ieee") String ieee,@Param("ep") Integer ep);
    Device getOneById(@Param("id") Integer id);
    int getCountByIeeeAndCids(@Param("ieee") String ieee,@Param("cids") Collection<Integer> cids);
    int updateNameAndLinkStatusByIeeeAndEp(@Param("name") String name, @Param("linkStatus") Boolean linkStatus, @Param("ieee") String ieee, @Param("ep") Integer ep);
    int saveOrUpdateByIeeeAndEp(DeviceEp deviceEp);
    int updateNameAndLinkStatusAndNetState(DeviceEp deviceEp);
    int insertByDeviceEp(DeviceEp deviceEp);
}




