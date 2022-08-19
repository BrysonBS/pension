package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.nursing.domain.po.NursingOrderSetting;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.mapper.NursingOrderSettingMapper;
import com.ruoyi.pension.owon.domain.po.SysDeptOwon;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author Administrator
* @description 针对表【nursing_order_setting】的数据库操作Service
* @createDate 2022-07-30 10:37:50
*/
@Service
public class NursingOrderSettingService extends ServiceImpl<NursingOrderSettingMapper, NursingOrderSetting> implements IService<NursingOrderSetting> {
    @Autowired
    private ISysDeptService sysDeptService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysDeptOwonService sysDeptOwonService;

    public NursingOrderSetting getOneOrAncestorByDeptId(Long deptId){
        //先尝试从缓存中获取
        NursingOrderSetting nursingOrderSetting = redisCache.getCacheObject(PensionBusiness.getKeyOfOrderSetting(deptId));
        if(nursingOrderSetting != null) return nursingOrderSetting;

        //否则从数据库获取
        Map<Long,NursingOrderSetting> orderSettingMap = list().stream().collect(Collectors.toMap(NursingOrderSetting::getDeptId, Function.identity()));
        ArrayDeque<Long> stack = sysDeptOwonService.getDeptIdAndAncestorsStack(deptId);
        while(nursingOrderSetting == null && !stack.isEmpty())
            nursingOrderSetting = orderSettingMap.get(stack.pop());

        //存入redis缓存
        redisCache.setCacheObject(PensionBusiness.getKeyOfOrderSetting(deptId),nursingOrderSetting,1, TimeUnit.DAYS);
        return nursingOrderSetting;
    }

    @Override
    public boolean updateById(NursingOrderSetting entity) {
        //先刷新缓存
        refreshCache(entity.getDeptId());
        return super.updateById(entity);
    }

    public void refreshCache(Long deptId){
        redisCache.deleteObject(sysDeptOwonService
                .getListDeptAndChildrenByDeptId(deptId)
                .stream()
                .map(id -> PensionBusiness.getKeyOfOrderSetting(deptId))
                .toList());
    }
}
