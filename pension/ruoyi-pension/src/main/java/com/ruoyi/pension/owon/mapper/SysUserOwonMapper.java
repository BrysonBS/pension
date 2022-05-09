package com.ruoyi.pension.owon.mapper;

import com.ruoyi.pension.owon.domain.po.SysUserOwon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sys_user(用户信息表)】的数据库操作Mapper
* @createDate 2022-04-22 09:59:02
* @Entity com.ruoyi.pension.owon.domain.po.SysUserOwon
*/
public interface SysUserOwonMapper extends BaseMapper<SysUserOwon> {
    List<SysUserOwon> getUsersByDeptIdOfSuperiors(@Param("deptId") Long deptId);
}




