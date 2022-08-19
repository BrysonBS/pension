package com.ruoyi.pension.nursing.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.pension.common.api.OrderNumberManager;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.enums.TableEnum;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.ruoyi.pension.common.service.PensionUploadService;
import com.ruoyi.pension.nursing.domain.po.NursingOrder;
import com.ruoyi.pension.nursing.domain.po.NursingRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.nursing.domain.po.NursingServiceItems;
import com.ruoyi.pension.nursing.domain.vo.NursingRecordVo;
import com.ruoyi.pension.nursing.mapper.NursingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private NursingServiceItemsService nursingServiceItemsService;
    @Autowired
    private PensionUploadService pensionUploadService;
    @Autowired
    private NursingOrderService nursingOrderService;

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
        //更新状态订单状态
        NursingOrder order = nursingOrderService.getById(record.getOrderId());
        order.setRecordId(record.getId());
        if(pensionUploadService.saveBathEntity(list)
                && nursingOrderService.updateRecordId(order)
                && nursingOrderService.updateComplete(order)) return true;
        throw new RuntimeException("保存失败");
    }
    @PensionDataScope
    public List<NursingRecord> getListByExample(NursingRecord nursingRecord){
        return this.baseMapper.getListByExample(nursingRecord);
    }
    @PensionDataScope(deptAlias = "a",userAlias = "a")
    public List<NursingRecordVo> getListVoByExample(NursingRecord nursingRecord){
        return this.baseMapper.getListVoByExample(nursingRecord);
    }
    @Transactional
    public boolean removeEntityBatch(List<Integer> ids){
        //先删除附件,再更新订单护理记录状态,最后删除记录
        if(ids != null && !ids.isEmpty()
                && pensionUploadService.removeByTableEnumAndRelateIds(TableEnum.NURSING_RECORD,ids)
                && nursingOrderService.updateRecordIdToNull(this.baseMapper.getListOrderIdByIds(ids))
                && this.removeBatchByIds(ids)) return true;
        throw new RuntimeException("删除失败");
    }
}
