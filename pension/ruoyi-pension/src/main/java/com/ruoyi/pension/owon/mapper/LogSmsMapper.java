package com.ruoyi.pension.owon.mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;

import com.ruoyi.pension.owon.domain.po.LogSms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【log_sms】的数据库操作Mapper
* @createDate 2022-04-23 16:42:38
* @Entity com.ruoyi.pension.owon.domain.po.LogSms
*/
public interface LogSmsMapper extends BaseMapper<LogSms> {
    int deleteBySendTimeLessThan(@Param("sendTime") LocalDateTime sendTime);
}




