package com.ruoyi.pension.nursing.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.nursing.domain.po.NursingServiceItems;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【nursing_service_items】的数据库操作Mapper
* @createDate 2022-05-30 13:41:15
* @Entity com.ruoyi.pension.nursing.domain.po.NursingServiceItems
*/
public interface NursingServiceItemsMapper extends BaseMapper<NursingServiceItems> {
    List<NursingServiceItems> getAllByRelateId(@Param("relateId") Integer relateId);
    List<String> getServiceIdsByRelateId(@Param("relateId") Integer relateId);
}




