package com.ruoyi.pension.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.api.OssManager;
import com.ruoyi.pension.common.domain.enums.TableEnum;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.common.mapper.PensionUploadMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
* @author Administrator
* @description 针对表【pension_upload】的数据库操作Service
* @createDate 2022-05-30 13:46:01
*/
@Service
public class PensionUploadService extends ServiceImpl<PensionUploadMapper, PensionUpload> implements IService<PensionUpload> {
    @Autowired
    private OssManager ossManager;
    public List<PensionUpload> getAllByRelateId(Integer relateId){
        return this.baseMapper.getAllByRelateId(relateId);
    }
    public List<PensionUpload> getByTableEnumAndRelateId(@NotNull TableEnum tableEnum,Integer relateId){
        return this.baseMapper.getByTableEnumAndRelateId(tableEnum,relateId);
    }
    public List<PensionUpload> getByTableEnumAndRelateIds(@NotNull TableEnum tableEnum,Collection<Integer> relateIds){
        return this.baseMapper.getByTableEnumAndRelateIds(tableEnum,relateIds);
    }
    public void download(PensionUpload pensionUpload, HttpServletResponse response){
        ossManager.downloadByStream(pensionUpload.getRootPath() + pensionUpload.getUri(),pensionUpload.getFileName(),response);
    }

    @Transactional
    public boolean saveBathEntity(List<PensionUpload> list) throws IOException {
        if(list == null || list.isEmpty()) return true;
        //先存表
        if(this.saveBatch(list)) {
            //成功则将附件保存到oss云
            for (PensionUpload upload : list) {
                try (InputStream input = upload.getAtt().getInputStream()) {
                    if (!ossManager.uploadByStream(upload.getRootPath() + upload.getUri(), input))
                        throw new RuntimeException("文件上传失败!");
                }
            }
            return true;
        }
        return false;
    }
    @Transactional
    public boolean saveEntity(PensionUpload pensionUpload) throws IOException {
        if(save(pensionUpload)){//成功则存储到oss云
            try(InputStream input = pensionUpload.getAtt().getInputStream()){
                if(!ossManager.uploadByStream(pensionUpload.getRootPath() + pensionUpload.getUri(),input))
                    throw new RuntimeException("文件上传失败!");
            }
            return true;
        }
        return false;
    }
    @Transactional
    public boolean removeEntity(PensionUpload pensionUpload){
        //先删表记录
        if(this.removeById(pensionUpload.getId())){
            //再从oss云中删除文件
            if(pensionUpload.getUri() != null)
                ossManager.removeObject(pensionUpload.getRootPath() + pensionUpload.getUri());
            return true;
        }
        return false;
    }
    public boolean removeByTableEnumAndRelateIds(@NotNull TableEnum tableEnum, Collection<Integer> relateIds){
        //先获取上传列表从OSS云删除文件
        Optional.ofNullable(this.getByTableEnumAndRelateIds(tableEnum,relateIds))
                .orElseGet(List::of)
                .stream().map(pensionUpload -> pensionUpload.getRootPath()+pensionUpload.getUri())
                .forEach(ossManager::removeObject);
        //再删除列表记录
        this.baseMapper.removeByTableEnumAndRelateIds(tableEnum,relateIds);
        return true;
    }
}
