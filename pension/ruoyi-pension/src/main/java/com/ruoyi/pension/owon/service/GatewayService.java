package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.owon.domain.po.Gateway;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.GatewayMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_gateway】的数据库操作Service
* @createDate 2022-04-15 16:23:55
*/
@Service
public class GatewayService extends ServiceImpl<GatewayMapper, Gateway> implements IService<Gateway> {
    public List<String> selectAllByDeptIds(List<Long> deptIds){
        return this.baseMapper.selectAllByDeptIds(deptIds);
    }
    public Gateway getOneByGwCode(String mac){
        return this.baseMapper.getOneByGwCode(mac);
    }
    @PensionDataScope(ignoreUser = true)
    public List<Gateway> getListByExample(Gateway gateway){
        return this.baseMapper.getListByExample(gateway);
    }
}
