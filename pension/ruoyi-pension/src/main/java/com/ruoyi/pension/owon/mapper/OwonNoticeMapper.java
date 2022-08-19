package com.ruoyi.pension.owon.mapper;

import com.ruoyi.pension.owon.domain.po.OwonNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_notice】的数据库操作Mapper
* @createDate 2022-04-21 11:44:47
* @Entity com.ruoyi.pension.owon.domain.po.OwonNotice
*/
public interface OwonNoticeMapper extends BaseMapper<OwonNotice> {
    List<OwonNotice> getListByDeptId(OwonNotice notice);
}




