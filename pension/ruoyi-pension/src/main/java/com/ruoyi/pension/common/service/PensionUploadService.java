package com.ruoyi.pension.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.common.mapper.PensionUploadMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【pension_upload】的数据库操作Service
* @createDate 2022-05-30 13:46:01
*/
@Service
public class PensionUploadService extends ServiceImpl<PensionUploadMapper, PensionUpload> implements IService<PensionUpload> {
    public List<PensionUpload> getAllByRelateId(Integer relateId){
        return this.baseMapper.getAllByRelateId(relateId);
    }
}
