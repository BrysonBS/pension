package com.ruoyi.pension.owon.service;

import com.ruoyi.pension.owon.domain.dto.Response;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import com.ruoyi.pension.owon.mapper.ResponseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class ResponseService extends ServiceImpl<ResponseMapper, Response> implements IService<Response> {
    @Autowired
    private DeviceEpService deviceEpService;
    @Transactional
    public boolean saveCascade(Response response){
        boolean result = this.save(response);
        List<?> epList = response.getEpList();
        if(epList != null && !epList.isEmpty() && result){
            if(epList.get(0) instanceof DeviceEp) {
                Collection<DeviceEp> eps = (Collection<DeviceEp>) epList;
                eps.forEach(e -> e.setRespId(response.getId()));
                result = deviceEpService.saveBatch(eps);
            }
        }
        return result;
    }
}
