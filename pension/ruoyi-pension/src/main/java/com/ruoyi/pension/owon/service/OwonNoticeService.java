package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.owon.domain.po.OwonNotice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.mapper.OwonNoticeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author Administrator
* @description 针对表【owon_notice】的数据库操作Service
* @createDate 2022-04-21 11:44:47
*/
@Service
public class OwonNoticeService extends ServiceImpl<OwonNoticeMapper, OwonNotice> implements IService<OwonNotice> {
    @PensionDataScope(ignoreUser = true)
    public List<OwonNotice> getListByExample(OwonNotice owonNotice){
        return this.baseMapper.getListByDeptId(owonNotice);
    }
}
