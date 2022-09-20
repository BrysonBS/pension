package com.ruoyi.pension.common.mapper;

import com.ruoyi.pension.common.domain.vo.ExampleVo;

import java.util.List;

public interface ScreenMapper {
    List<ExampleVo> getListReportCount();

    List<ExampleVo> getListItemsTotal(ExampleVo exampleVo);

    List<ExampleVo> getListStatusCount(ExampleVo exampleVo);

    List<ExampleVo> getListNursingCount();

    List<ExampleVo> getListOrderAmountCount(ExampleVo exampleVo);

    List<ExampleVo> getListNursingCountOfDashBoard(ExampleVo exampleVo);

    List<ExampleVo> getListOrderOfDay(ExampleVo exampleVo);

    List<ExampleVo> getListNotice(ExampleVo exampleVo);
}
