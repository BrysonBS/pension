package com.ruoyi.pension.bioland.mapper;

import com.ruoyi.pension.bioland.domain.po.SrcDataValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author Administrator
* @description 针对表【bioland_srcdata】的数据库操作Mapper
* @createDate 2022-05-13 15:24:44
* @Entity com.ruoyi.pension.bioland.domain.po.SrcDataValue
*/
public interface SrcDataValueMapper extends BaseMapper<SrcDataValue> {
    List<SrcDataValue> listLatest(@Param("serialNumber") String serialNumber,
                                  @Param("beginTime") LocalDateTime beginTime,
                                  @Param("endTime") LocalDateTime endTime);
}




