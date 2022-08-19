package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.api.OrderNumberManager;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.enums.TableEnum;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.ruoyi.pension.common.service.PensionUploadService;
import com.ruoyi.pension.nursing.domain.po.NursingWorker;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.mapper.NursingWorkerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

/**
* @author Administrator
* @description 针对表【nursing_worker】的数据库操作Service
* @createDate 2022-06-24 10:36:14
*/
@Service
public class NursingWorkerService extends ServiceImpl<NursingWorkerMapper, NursingWorker> implements IService<NursingWorker> {
    @Autowired
    private PensionUploadService pensionUploadService;
    @Autowired
    private OrderNumberManager orderNumberManager;
    @PensionDataScope(deptAlias = "a",userAlias = "a")
    public List<NursingWorker> getListByExample(NursingWorker nursingWorker) {
        if(nursingWorker.getStatusList() != null && nursingWorker.getStatusList().isEmpty())
            nursingWorker.setStatusList(null);
        return this.baseMapper.getListByExample(nursingWorker);
    }
    public PensionUpload addCertificate(Integer relateId,MultipartFile att) throws IOException {
        TableEnum tableEnum = TableEnum.NURSING_WORKER_CERTIFICATE;
        if(att == null || att.isEmpty()) return null;

        String rootPath = PensionBusiness.NURSING_WORKER_CERTIFICATE_ROOT;
        String uri = orderNumberManager.getOrderNumber(PensionBusiness.PENSION_UPLOAD_KEY)
                + att.getOriginalFilename();
        PensionUpload pensionUpload = PensionUpload.builder()
                .att(att)//证书
                .masterId(tableEnum.getCode())
                .masterName(tableEnum.getName())
                .relateId(relateId)
                .rootPath(rootPath)
                .fileName(att.getOriginalFilename())
                .fileType(att.getContentType())
                .fileSize(att.getSize())
                .uploadTime(LocalDateTime.now())
                .uri(uri)
                .build();
        //保存
        return pensionUploadService.saveEntity(pensionUpload) && pensionUpload.getId() != null
                ? pensionUpload : null;
    }

    public boolean removeEntityBatchByIds(Collection<Integer> ids){
        //先删除上传的证书
        if(pensionUploadService.removeByTableEnumAndRelateIds(TableEnum.NURSING_WORKER_CERTIFICATE,ids))
            return this.removeBatchByIds(ids);//再删除记录
        return false;
    }

    public boolean updateStatusByIdAndStatus(Integer oldStatus,Integer newStatus,Integer id){
        return updateStatusByIdsAndStatus(oldStatus,newStatus,List.of(id));
    }
    @Transactional
    public boolean updateStatusByIdsAndStatus(Integer oldStatus,Integer newStatus,List<Integer> ids){
        LambdaUpdateWrapper<NursingWorker> workerUpdateWrapper = new LambdaUpdateWrapper<>();
        workerUpdateWrapper.set(NursingWorker::getStatus,newStatus)
                .in(NursingWorker::getId,ids)
                .eq(NursingWorker::getStatus,oldStatus)
                .eq(NursingWorker::getDelFlag,"0");
        return super.update(workerUpdateWrapper);
    }
}
