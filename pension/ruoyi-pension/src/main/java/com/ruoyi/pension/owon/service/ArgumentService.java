package com.ruoyi.pension.owon.service;

import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.po.PanicButton;
import com.ruoyi.pension.owon.mapper.ArgumentMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class ArgumentService extends ServiceImpl<ArgumentMapper, Argument> implements IService<Argument> {
    @Autowired
    private PanicButtonService panicButtonService;
    @Transactional
    public boolean saveCascade(Argument argument) {
        boolean result = this.save(argument);
        List<?> epList = argument.getEpList();
        List<?> rlist = argument.getRlist();
        int argId = argument.getId();
        if(epList != null && !epList.isEmpty()){
            //TODO eplist
        }
        if(rlist != null && !rlist.isEmpty()){
            Object obj = rlist.get(0);
            if(obj instanceof PanicButton){
                ((List<PanicButton>)rlist).forEach(e -> e.setArgId(argId));
                result = panicButtonService.saveBatch((Collection<PanicButton>) rlist);
            }
        }
        return result;
    }
}
