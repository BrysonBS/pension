package com.ruoyi.pension.nursing.mapper;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.nursing.domain.po.NursingServicePrice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;
import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_service_price】的数据库操作Mapper
* @createDate 2022-07-28 13:51:47
* @Entity com.ruoyi.pension.nursing.domain.po.NursingServicePrice
*/
public interface NursingServicePriceMapper extends BaseMapper<NursingServicePrice> {
    List<NursingServicePrice> getListByExample(NursingServicePrice nursingServicePrice);
    List<NursingServicePrice> getListByDeptIdAndNow(@Param("deptId") Long deptId);
    List<NursingServicePrice> getListByDeptIdsAndNow(@Param("deptIds") Collection<Long> deptIds);
}




