package com.ruoyi.pension.owon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.dto.Response;
import com.ruoyi.pension.owon.mapper.DatapacketMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DatapacketService extends ServiceImpl<DatapacketMapper, Datapacket> implements IService<Datapacket> {
    @Autowired
    private ArgumentService argumentService;
    @Autowired
    private ResponseService responseService;
    @Transactional
    public boolean saveCascade(Datapacket<?,?> datapacket){
        boolean result = this.save(datapacket);
        Object argument = datapacket.getArgument();
        Object response = datapacket.getResponse();
        if(argument instanceof Argument) {
            ((Argument<?, ?>) argument).setDpId(datapacket.getId());
            result =  argumentService.saveCascade((Argument) argument);
        }
        if(response instanceof Response){
            ((Response<?>) response).setDpId(datapacket.getId());
            result =  responseService.saveCascade((Response) response);
        }
        return result;
    }
}
