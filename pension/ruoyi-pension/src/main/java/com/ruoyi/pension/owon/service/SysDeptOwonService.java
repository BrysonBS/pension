package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.owon.domain.po.Device;
import com.ruoyi.pension.owon.domain.po.SysDeptOwon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.SysDeptOwonMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【sys_dept(部门表)】的数据库操作Service
* @createDate 2022-04-16 14:47:45
*/
@Service
public class SysDeptOwonService extends ServiceImpl<SysDeptOwonMapper, SysDeptOwon> implements IService<SysDeptOwon> {
    /**
     * 根据部门id获取其下所有部门id列表(含本身)
     * @param deptId
     * @return
     */
    public List<Long> getListByDeptId(Long deptId){
        return this.baseMapper.getListByDeptId(deptId);
    }

    public String getFullNameByDeptId(long deptId){
        SysDeptOwon sysDeptOwon = this.baseMapper.selectById(deptId);
        String ancestors = sysDeptOwon.getAncestors();
        List<Long> deptIds = Arrays.stream(ancestors.split(","))
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
        LambdaQueryWrapper<SysDeptOwon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDeptOwon::getDeptId, deptIds);
        return baseMapper.selectList(queryWrapper).stream()
                .sorted((e1,e2) -> (int) (e1.getDeptId() - e2.getDeptId()))
                .reduce("",
                    (e1,e2) -> e1 + e2.getDeptName() + "/",
                    (e1,e2) -> null
                )+sysDeptOwon.getDeptName();
    }
}
