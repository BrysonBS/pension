package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.owon.domain.po.SysUserOwon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.SysUserOwonMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @createDate 2022-04-22 09:59:02
*/
@Service
public class SysUserOwonService extends ServiceImpl<SysUserOwonMapper, SysUserOwon> implements IService<SysUserOwon> {
    /**
     * 根据部门id获取到当前部门及其上级部门下所有用户(不含顶级部门(100)下用户)
     * @param deptId
     * @return
     */
    public List<SysUserOwon> getUsersByDeptIdOfSuperiors(long deptId){
        return this.baseMapper.getUsersByDeptIdOfSuperiors(deptId);
    }
}
