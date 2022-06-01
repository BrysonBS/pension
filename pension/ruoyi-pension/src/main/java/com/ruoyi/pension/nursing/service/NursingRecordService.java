package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.api.OrderNumberManager;
import com.ruoyi.pension.common.api.OssManager;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.enums.TableEnum;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.ruoyi.pension.common.service.PensionUploadService;
import com.ruoyi.pension.nursing.domain.po.NursingRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.domain.po.NursingServiceItems;
import com.ruoyi.pension.nursing.mapper.NursingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【nursing_record】的数据库操作Service
* @createDate 2022-05-30 09:02:23
*/
@Service
public class NursingRecordService extends ServiceImpl<NursingRecordMapper, NursingRecord> implements IService<NursingRecord> {
    @Autowired
    private OrderNumberManager orderNumberManager;
    @Autowired
    private OssManager ossManager;
    @Autowired
    private NursingServiceItemsService nursingServiceItemsService;
    @Autowired
    private PensionUploadService pensionUploadService;

    @Transactional
    public boolean saveCascade(NursingRecord record, MultipartFile[] attachments) throws IOException {
        //先保存主表
        boolean result = save(record);
        if(!result) throw new RuntimeException("保存失败");
        //再保存从表: 服务项目
        TableEnum tableEnum = TableEnum.NURSING_RECORD;

        List<NursingServiceItems> listItems =
                Arrays.stream(record.getDictServiceIds())
                        .map(id -> NursingServiceItems.builder()
                                .masterId(tableEnum.getCode())
                                .masterName(tableEnum.getName())//关联主表表名
                                .relateId(record.getId())//关联主表主键
                                .serviceId(id)
                                .build()
                        )
                        .toList();
        result = nursingServiceItemsService.saveBatch(listItems);
        if(!result) throw new RuntimeException("保存失败");

        //保存附件
        List<PensionUpload> list = new ArrayList<>();
        for(MultipartFile att : attachments){
            if(att == null || att.isEmpty()) continue;
            String rootPath = PensionBusiness.NURSING_RECORD_ROOT;
            String uri = orderNumberManager.getOrderNumber(PensionBusiness.PENSION_UPLOAD_KEY)
                    + att.getOriginalFilename();
            list.add(
                PensionUpload.builder()
                    .att(att)//附件
                    .masterId(tableEnum.getCode())
                    .masterName(tableEnum.getName())
                    .relateId(record.getId())
                    .rootPath(rootPath)
                    .fileName(att.getOriginalFilename())
                    .fileType(att.getContentType())
                    .fileSize(att.getSize())
                    .uploadTime(LocalDateTime.now())
                    .uri(uri)
                    .build()
            );
        }
        //先存表
        pensionUploadService.saveBatch(list);
        //再将附件保存到oss云
        for(PensionUpload upload : list){
            try(InputStream input = upload.getAtt().getInputStream()){
                if(!ossManager.uploadByStream(upload.getRootPath() + upload.getUri(),input))
                    throw new RuntimeException("文件上传失败!");
            }
        }
        return true;
    }
    @PensionDataScope
    public List<NursingRecord> getListByExample(NursingRecord nursingRecord){
        return this.baseMapper.getListByExample(nursingRecord);
    }
}
