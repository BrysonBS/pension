package com.ruoyi.pension.common.mapper;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.common.domain.po.PensionPayment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【pension_payment】的数据库操作Mapper
* @createDate 2022-06-30 15:59:44
* @Entity com.ruoyi.pension.common.domain.po.PensionPayment
*/
public interface PensionPaymentMapper extends BaseMapper<PensionPayment> {
    List<PensionPayment> listByExample(PensionPayment pensionPayment);
    PensionPayment getOneByDeptIdAndPayType(@Param("deptId") Long deptId,@Param("payType") Integer payType);
    List<PensionPayment> getListByDeptIdAndPayType(@Param("deptId") Long deptId,@Param("payType") Integer payType);
}




