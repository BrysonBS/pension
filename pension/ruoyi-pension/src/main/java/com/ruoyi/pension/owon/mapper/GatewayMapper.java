package com.ruoyi.pension.owon.mapper;

import com.ruoyi.pension.owon.domain.po.Gateway;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_gateway】的数据库操作Mapper
* @createDate 2022-04-15 16:23:55
* @Entity com.ruoyi.pension.owon.domain.po.Gateway
*/
public interface GatewayMapper extends BaseMapper<Gateway> {
    List<String> selectAllByDeptIds(@Param("deptIds") List<Long> deptIds);

    Gateway getOneByGwCode(@Param("gwCode") String gwCode);
    List<Gateway> getListByExample(Gateway gateway);
}




