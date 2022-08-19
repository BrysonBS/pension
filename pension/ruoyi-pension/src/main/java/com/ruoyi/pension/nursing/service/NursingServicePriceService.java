package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.nursing.domain.po.NursingServicePrice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.mapper.NursingServicePriceMapper;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【nursing_service_price】的数据库操作Service
* @createDate 2022-07-28 13:51:47
*/
@Service
public class NursingServicePriceService extends ServiceImpl<NursingServicePriceMapper, NursingServicePrice> implements IService<NursingServicePrice> {
    @Autowired
    private ISysDeptService sysDeptService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysDeptOwonService sysDeptOwonService;

    @PensionDataScope(deptAlias = "a",userAlias = "a")
    public List<NursingServicePrice> getListByExample(NursingServicePrice nursingServicePrice){
        return this.baseMapper.getListByExample(nursingServicePrice);
    }
    public List<NursingServicePrice> getListOrAncestorByDeptId(Long deptId){
        //先尝试从缓存中获取
        List<NursingServicePrice> list = redisCache.getCacheObject(PensionBusiness.getKeyOfServicePrice(deptId));
        if(list != null) return list;
        //否则从数据库查找
        ArrayDeque<Long> stack = sysDeptOwonService.getDeptIdAndAncestorsStack(deptId);
        List<NursingServicePrice> priceList = this.getListByDeptIdsAndNow(stack.stream().toList());
        if(priceList.isEmpty()) return priceList;
        Map<Long,List<NursingServicePrice>> priceMap = priceList.stream().collect(Collectors.groupingBy(NursingServicePrice::getDeptId));
        while(list == null && !stack.isEmpty())
            list = priceMap.get(stack.pop());

        //存入缓存
        if(list != null && !list.isEmpty()) {
            long number = (list.stream()
                    .map(NursingServicePrice::getPeriodEnd)
                    .min((a,b) -> a.isEqual(b) ? 0 : (a.isAfter(b) ? 1 : -1))
                    .orElse(LocalDateTime.now())
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    - System.currentTimeMillis())/60000;
            if(number > 0)
                redisCache.setCacheObject(PensionBusiness.getKeyOfServicePrice(deptId), list, Long.valueOf(number).intValue(), TimeUnit.MINUTES);
        }
        return list;
    }

    /**
     * 根据部门id列表获取所有对应价格表
     * @param deptIds 部门列表
     * @return 价格列表
     */
    public List<NursingServicePrice> getListByDeptIdsAndNow(Collection<Long> deptIds){
        if(deptIds == null || deptIds.isEmpty()) return List.of();
        return this.baseMapper.getListByDeptIdsAndNow(deptIds);
    }

    @Override
    public boolean updateById(NursingServicePrice entity) {
        //先清除缓存
        refreshCache(entity.getDeptId());
        return super.updateById(entity);
    }

    @Override
    public boolean save(NursingServicePrice entity) {
        //先清除缓存
        refreshCache(entity.getDeptId());
        return super.save(entity);
    }

    /**
     * 刷新缓存
     * @param deptId
     */
    public void refreshCache(Long deptId){
        redisCache.deleteObject(sysDeptOwonService
                .getListDeptAndChildrenByDeptId(deptId)
                .stream()
                .map(id -> PensionBusiness.getKeyOfServicePrice(deptId))
                .toList());
    }
}
