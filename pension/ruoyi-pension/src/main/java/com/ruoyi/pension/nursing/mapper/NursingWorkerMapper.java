package com.ruoyi.pension.nursing.mapper;

import com.ruoyi.pension.nursing.domain.po.NursingWorker;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_worker】的数据库操作Mapper
* @createDate 2022-06-24 10:36:14
* @Entity com.ruoyi.pension.nursing.domain.po.NursingWorker
*/
public interface NursingWorkerMapper extends BaseMapper<NursingWorker> {
    List<NursingWorker> getListByExample(NursingWorker nursingWorker);
}




