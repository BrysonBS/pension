package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.nursing.domain.po.NursingPerson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.mapper.NursingPersonMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
* @author Administrator
* @description 针对表【nursing_person】的数据库操作Service
* @createDate 2022-05-24 08:40:57
*/
@Service
public class NursingPersonService extends ServiceImpl<NursingPersonMapper, NursingPerson> implements IService<NursingPerson> {
   @PensionDataScope
   public List<NursingPerson> getListByExample(NursingPerson person){
      return this.baseMapper.getListByExample(person);
   }
   @PensionDataScope
   public List<NursingPerson> getListPerson(NursingPerson nursingPerson){
      return this.baseMapper.getListPerson(nursingPerson);
   }
   @PensionDataScope
   public List<NursingPerson> getListOwnByUserId(NursingPerson nursingPerson){
      return this.baseMapper.getListOwnByUserId(nursingPerson);
   }

   @Override
   public NursingPerson getById(Serializable id) {
      NursingPerson nursingPerson = super.getById(id);
      nursingPerson.setFullAddress(
              Stream.of(nursingPerson.getProvince(),nursingPerson.getCity(),nursingPerson.getDistrict(),nursingPerson.getAddress())
                              .filter(Objects::nonNull)
                                      .collect(Collectors.joining()));
      return nursingPerson;
   }


   /**
    * 大屏展示查询: 失能等级,城市,坐标
    * @param nursingPerson
    * @return
    */
   @PensionDataScope(deptAlias = "a",userAlias = "a")
   public List<NursingPerson> getListScreen(NursingPerson nursingPerson){
      return this.baseMapper.getListScreen(nursingPerson);
   }
}
