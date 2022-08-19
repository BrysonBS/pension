package com.ruoyi.pension.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pension.common.domain.vo.SelectOptionVo;

import java.util.List;
import java.util.Map;

public interface SelectOptionMapper extends BaseMapper<SelectOptionVo<Object,Object>> {
    List<SelectOptionVo<Long,Long>> getUserOptions(Map<String, String> params);
}
