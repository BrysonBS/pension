package com.ruoyi.pension.common.service;

import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.vo.ExampleVo;
import com.ruoyi.pension.common.mapper.ScreenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScreenService {
    @Autowired
    private ScreenMapper screenMapper;

    public List<ExampleVo> getListReportCount(){
        return screenMapper.getListReportCount();
    }
    @PensionDataScope
    public List<ExampleVo> getListItemsTotal(ExampleVo exampleVo){
        return screenMapper.getListItemsTotal(exampleVo);
    }
    @PensionDataScope(deptAlias = "a",userAlias = "a")
    public List<ExampleVo> getListStatusCount(ExampleVo exampleVo){
        return screenMapper.getListStatusCount(exampleVo);
    }
    public List<ExampleVo> getListNursingCount(){
        return screenMapper.getListNursingCount();
    }
    @PensionDataScope(deptAlias = "a",userAlias = "a")
    public List<ExampleVo> getListOrderAmountCount(ExampleVo exampleVo){
        return screenMapper.getListOrderAmountCount(exampleVo);
    }

    @PensionDataScope(ignoreUser = true)
    public List<ExampleVo> getListNursingCount(ExampleVo exampleVo){
        return screenMapper.getListNursingCountOfDashBoard(exampleVo);
    }
    @PensionDataScope
    public List<ExampleVo> getListOrderOfDay(ExampleVo exampleVo){
        return screenMapper.getListOrderOfDay(exampleVo);
    }
    @PensionDataScope(ignoreUser = true)
    public List<ExampleVo> getListNotice(ExampleVo exampleVo){
        return screenMapper.getListNotice(exampleVo);
    }
}
