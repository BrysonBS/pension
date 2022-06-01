package com.ruoyi.pension.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.domain.po.LogSms;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.common.mapper.LogSmsMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
* @author Administrator
* @description 针对表【log_sms】的数据库操作Service
* @createDate 2022-04-23 16:42:38
*/
@Service
public class LogSmsService extends ServiceImpl<LogSmsMapper, LogSms> implements IService<LogSms> {
    public void deleteBySendTimeLessThan(LocalDateTime sendTime){
        this.baseMapper.deleteBySendTimeLessThan(sendTime);
    }
}
