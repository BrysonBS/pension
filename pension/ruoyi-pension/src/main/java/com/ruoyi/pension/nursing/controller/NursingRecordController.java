package com.ruoyi.pension.nursing.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.pension.common.api.OrderNumberManager;
import com.ruoyi.pension.common.api.OssManager;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.ruoyi.pension.common.service.PensionUploadService;
import com.ruoyi.pension.nursing.domain.po.NursingRecord;
import com.ruoyi.pension.nursing.domain.po.NursingServiceItems;
import com.ruoyi.pension.nursing.service.NursingRecordService;
import com.ruoyi.pension.nursing.service.NursingPersonService;
import com.ruoyi.pension.nursing.service.NursingServiceItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/nursing/record")
public class NursingRecordController extends BaseController {
    @Autowired
    private OssManager ossManager;
    @Autowired
    private OrderNumberManager orderNumberManager;
    @Autowired
    private NursingRecordService nursingRecordService;
    @Autowired
    private NursingServiceItemsService nursingServiceItemsService;
    @Autowired
    private PensionUploadService pensionUploadService;
    @GetMapping("/init")
    public AjaxResult getRecordInit(){
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser == null ? null : loginUser.getUser();
        SysDept currentDept = currentUser == null ? null : currentUser.getDept();
        if(currentUser == null || currentDept == null)
            return AjaxResult.error("用户基本信息有误!");
        //申请人信息
        NursingRecord record = NursingRecord.builder()
                .userId(currentUser.getUserId())
                .applyName(currentUser.getNickName())
                .phone(currentUser.getPhonenumber())
                .deptName(currentDept.getDeptName())
                .deptId(currentDept.getDeptId())
                .applyTime(LocalDateTime.now())
                .build();
        return AjaxResult.success().put(AjaxResult.DATA_TAG,record);
    }

    @PostMapping(consumes = "multipart/form-data")
    public AjaxResult post(@RequestPart("record") @Valid NursingRecord record,
                           @RequestPart("attachments") MultipartFile[] attachments) throws IOException {
        if(record.getId() != null) return AjaxResult.error("已存在!");
        //开始结束时间校验
        if(record.getEndTime().isBefore(record.getBeginTime()))
            return AjaxResult.error("开始时间必须小于结束时间");
        //单号生成
        record.setOrderNumber(orderNumberManager.getOrderNumber(PensionBusiness.NURSING_RECORD));
        return toAjax(nursingRecordService.saveCascade(record,attachments));
    }

    @GetMapping("/listRecord")
    public TableDataInfo getList(NursingRecord nursingRecord){
        startPage();
        List<NursingRecord> list =  nursingRecordService.getListByExample(nursingRecord);
        return getDataTable(list);
    }
    @GetMapping("/detail/{id}")
    public AjaxResult getDetail(@PathVariable("id") Integer recordId){
        if(recordId == null) return AjaxResult.error();
        List<PensionUpload> uploads = pensionUploadService.getAllByRelateId(recordId);
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingServiceItemsService.getServiceIdsByRelateId(recordId))
                .put("uploads",uploads);
    }
}
