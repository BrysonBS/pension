package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.pension.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.nursing.domain.po.NursingPerson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.mapper.NursingPersonMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_person】的数据库操作Service
* @createDate 2022-05-24 08:40:57
*/
@Service
public class NursingPersonService extends ServiceImpl<NursingPersonMapper, NursingPerson> implements IService<NursingPerson> {
   @PensionDataScope
   public List<NursingPerson> getListByExample(NursingPerson person){
      List<NursingPerson> list = this.baseMapper.getListByExample(person);

      return list;
   }
}
