package com.ruoyi.pension.nursing.mapper;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.nursing.domain.po.NursingOrderSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【nursing_order_setting】的数据库操作Mapper
* @createDate 2022-07-30 10:37:50
* @Entity com.ruoyi.pension.nursing.domain.po.NursingOrderSetting
*/
public interface NursingOrderSettingMapper extends BaseMapper<NursingOrderSetting> {
    NursingOrderSetting getOneByDeptId(@Param("deptId") Long deptId);
}




