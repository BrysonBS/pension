package com.ruoyi.pension.nursing.mapper;

import com.ruoyi.pension.nursing.domain.po.NursingRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pension.nursing.domain.vo.NursingRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_record】的数据库操作Mapper
* @createDate 2022-05-30 09:02:23
* @Entity com.ruoyi.pension.nursing.domain.po.NursingRecord
*/
public interface NursingRecordMapper extends BaseMapper<NursingRecord> {
    List<NursingRecord> getListByExample(NursingRecord nursingRecord);
    List<NursingRecordVo> getListVoByExample(NursingRecord nursingRecord);
    List<Integer> getListOrderIdByIds(@Param("ids") Collection<Integer> ids);
}




