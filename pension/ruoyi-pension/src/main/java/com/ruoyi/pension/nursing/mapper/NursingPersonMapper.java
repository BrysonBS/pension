package com.ruoyi.pension.nursing.mapper;

import com.ruoyi.pension.nursing.domain.po.NursingPerson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_person】的数据库操作Mapper
* @createDate 2022-05-24 08:40:57
* @Entity com.ruoyi.pension.nursing.domain.po.NursingPerson
*/
public interface NursingPersonMapper extends BaseMapper<NursingPerson> {
    List<NursingPerson> getListByExample(NursingPerson person);
    List<NursingPerson> getListPerson(NursingPerson person);
    List<NursingPerson> getListOwnByUserId(NursingPerson person);
    List<NursingPerson> getListScreen(NursingPerson nursingPerson);
}




