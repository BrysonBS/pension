package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.owon.domain.po.Gateway;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.GatewayMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【owon_gateway】的数据库操作Service
* @createDate 2022-04-15 16:23:55
*/
@Service
public class GatewayService extends ServiceImpl<GatewayMapper, Gateway> implements IService<Gateway> {

}
