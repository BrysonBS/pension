package com.ruoyi.pension.owon.mapper;

import com.ruoyi.pension.owon.domain.dto.OwonReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【owon_report】的数据库操作Mapper
* @createDate 2022-04-08 10:00:03
* @Entity cn.feiaikeji.pension.owon.domain.dto.OwonReport
*/
public interface OwonReportMapper extends BaseMapper<OwonReport> {
    void ProcDeleteLtCreatedCascade(@Param("created") String created);
}




