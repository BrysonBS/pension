package com.ruoyi.pension.common.mapper;

import com.ruoyi.pension.common.domain.po.PensionRefundWechat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【pension_refund_wechat】的数据库操作Mapper
* @createDate 2022-08-11 15:57:56
* @Entity com.ruoyi.pension.common.domain.po.PensionRefundWechat
*/
public interface PensionRefundWechatMapper extends BaseMapper<PensionRefundWechat> {
    Integer getCountByRefundIdAndOutTradeNo(@Param("refundId") String refundId,@Param("outTradeNo") String outTradeNo);
}




