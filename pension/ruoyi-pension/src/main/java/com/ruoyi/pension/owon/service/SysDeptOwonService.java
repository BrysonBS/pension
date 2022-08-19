package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.owon.domain.po.SysDeptOwon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.SysDeptOwonMapper;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public List<Long> getListDeptAndChildrenByDeptId(Long deptId){
        return this.baseMapper.getListDeptAndChildrenByDeptId(deptId);
    }

    public String getFullNameByDeptId(Long deptId){
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
    public ArrayDeque<Long> getDeptIdAndAncestorsStack(Long deptId){
        ArrayDeque<Long> stack = getAncestorsStack(deptId);
        stack.push(deptId);
        return stack;
    }
    public ArrayDeque<Long> getDeptIdAndAncestorsStack(SysDeptOwon sysDeptOwon){
        ArrayDeque<Long> stack = getAncestorsStack(sysDeptOwon);
        stack.push(sysDeptOwon.getDeptId());
        return stack;
    }
    public ArrayDeque<Long> getAncestorsStack(Long deptId){
        SysDeptOwon sysDeptOwon = getById(deptId);
        return getAncestorsStack(sysDeptOwon);
    }
    public ArrayDeque<Long> getAncestorsStack(SysDeptOwon sysDeptOwon){
        if(sysDeptOwon == null) throw new RuntimeException("部门不存在");
        ArrayDeque<Long> stack = new ArrayDeque<>();
        Arrays.stream(sysDeptOwon.getAncestors().split(","))
                .mapToLong(Long::parseLong)
                .filter(e -> e != 0)
                .forEachOrdered(stack::push);
        return stack;
    }
    public List<SysDeptOwon> getListByDeptIds(Collection<Long> deptIds){
        if(deptIds == null || deptIds.isEmpty()) return List.of();
        return super.list(new LambdaQueryWrapper<SysDeptOwon>()
                .eq(SysDeptOwon::getDelFlag,"0")
                .in(SysDeptOwon::getDeptId,deptIds));
    }
    public List<SysDeptOwon> getAndAncestorsListByDeptId(Long deptId){
        return getListByDeptIds(getDeptIdAndAncestorsStack(deptId).stream().toList());
    }
}
