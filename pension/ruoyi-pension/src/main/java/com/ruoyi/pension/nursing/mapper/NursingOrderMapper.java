package com.ruoyi.pension.nursing.mapper;

import com.ruoyi.pension.nursing.domain.po.NursingOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
* @author Administrator
* @description 针对表【nursing_order】的数据库操作Mapper
* @createDate 2022-07-30 15:39:43
* @Entity com.ruoyi.pension.nursing.domain.po.NursingOrder
*/
public interface NursingOrderMapper extends BaseMapper<NursingOrder> {
    void updateStatusByIdAndStatus(@Param("id") Integer id, @Param("oldStatus") Integer oldStatus,@Param("newStatus") Integer newStatus);
    void commentFinishedOrder(@Param("id") Integer id,@Param("comment") String comment,@Param("star") Integer star);
    void updateStatusByOrderSn(@Param("status") Integer status, @Param("orderSn") String orderSn);
    void updateOrderRefundAmountByOrderNo(@Param("outTradeNo") String outTradeNo, @Param("refundAmount") BigDecimal refundAmount);
    List<NursingOrder> getListByExample(NursingOrder nursingOrder);
    NursingOrder getOneByOrderSn(@Param("orderSn") String orderSn);
    boolean updateRecordIdToNull(@Param("ids") Collection<Integer> ids);
}




