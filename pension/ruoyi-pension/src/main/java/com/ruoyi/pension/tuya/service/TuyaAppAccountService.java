package com.ruoyi.pension.tuya.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.tuya.domain.TuyaAppAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.tuya.domain.TuyaDevice;
import com.ruoyi.pension.tuya.mapper.TuyaAppAccountMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【tuya_app_account】的数据库操作Service
* @createDate 2022-10-17 17:37:20
*/
@Service
public class TuyaAppAccountService extends ServiceImpl<TuyaAppAccountMapper, TuyaAppAccount> implements IService<TuyaAppAccount>{

}
