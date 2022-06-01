package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.nursing.domain.po.NursingServiceItems;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.mapper.NursingServiceItemsMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_service_items】的数据库操作Service
* @createDate 2022-05-30 13:41:15
*/
@Service
public class NursingServiceItemsService extends ServiceImpl<NursingServiceItemsMapper, NursingServiceItems> implements IService<NursingServiceItems> {
    public List<NursingServiceItems> getAllByRelateId(Integer relateId){
        return this.baseMapper.getAllByRelateId(relateId);
    }
    public List<String> getServiceIdsByRelateId(Integer relateId){
        return this.baseMapper.getServiceIdsByRelateId(relateId);
    }
}
