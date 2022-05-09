package com.ruoyi.pension.owon.mapper;

import com.ruoyi.pension.owon.domain.po.SysDeptOwon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sys_dept(部门表)】的数据库操作Mapper
* @createDate 2022-04-16 14:47:45
* @Entity com.ruoyi.pension.owon.domain.po.SysDeptOwon
*/
public interface SysDeptOwonMapper extends BaseMapper<SysDeptOwon> {
    /**
     * 根据部门id获取其下所有部门id列表(含本身)
     * @param deptId
     * @return
     */
    List<Long> getListByDeptId(Long deptId);
}




