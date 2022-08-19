package com.ruoyi.pension.common.service;

import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.vo.SelectOptionVo;
import com.ruoyi.pension.common.mapper.SelectOptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectOptionService {
    @Autowired
    private SelectOptionMapper selectOptionMapper;
    @PensionDataScope
    public List<SelectOptionVo<Long,Long>> getUserOptions(SelectOptionVo<Long,Long> selectOptionVo){
        return selectOptionMapper.getUserOptions(selectOptionVo.getParams());
    }
}
