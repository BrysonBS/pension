package com.ruoyi.pension.common.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.pension.common.api.AlipayManager;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.common.domain.po.PensionPayment;
import com.ruoyi.pension.common.service.PensionPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/payment")
public class PensionPaymentController extends BaseController {
    @Autowired
    private PensionPaymentService pensionPaymentService;
    @Autowired
    private AlipayManager aliPayManager;
    @GetMapping("/merchant/list")
    public TableDataInfo list(PensionPayment pensionPayment){
        startPage();
        List<PensionPayment> list = pensionPaymentService.listByExample(pensionPayment);
        return getDataTable(list);
    }
    @GetMapping("/merchant/{id}")
    public AjaxResult get(@PathVariable("id") Integer id){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,pensionPaymentService.getById(id));
    }
    @GetMapping("/merchant/payType")
    public AjaxResult getByPayType(Integer payType){
        return AjaxResult.success()
                        .put(AjaxResult.DATA_TAG,
                                pensionPaymentService.getOneOrAncestorByDeptIdAndPayType(getDeptId(),payType));
    }
    @PreAuthorize("@ss.hasPermi('payment:merchant:add')")
    @PostMapping(value = "/merchant/add",consumes = "multipart/form-data")
    @Log(title = "支付管理/商户管理",businessType = BusinessType.INSERT)
    @OperationInfo(value = OperationInfo.Info.CREATED)
    public AjaxResult add(@RequestPart("record")PensionPayment pensionPayment,
                          @RequestPart("attachments") MultipartFile[] attachments) throws IOException, InvalidExtensionException {
        //不设置则默认为当前账号所在部门id
        if(pensionPayment.getDeptId() == null) pensionPayment.setDeptId(getDeptId());
        return pensionPaymentService.add(pensionPayment,attachments) ?
                AjaxResult.success() : AjaxResult.error();
    }
    @PostMapping(value = "/merchant/update",consumes = "multipart/form-data")
    @PreAuthorize("@ss.hasPermi('payment:merchant:edit')")
    @Log(title = "支付管理/商户管理", businessType = BusinessType.UPDATE)
    @OperationInfo(value = OperationInfo.Info.UPDATED)
    public AjaxResult update(@RequestPart("record")PensionPayment pensionPayment,
                             @RequestPart("attachments")MultipartFile[] attachments) throws IOException, InvalidExtensionException {
        if(pensionPayment.getId() == null)
            return AjaxResult.error("不存在,无法更新");

        return toAjax(pensionPaymentService.updateEntity(pensionPayment, attachments));
    }
    @PreAuthorize("@ss.hasPermi('payment:merchant:remove')")
    @DeleteMapping("/merchant/batch")
    @Log(title = "支付管理/商户管理",businessType = BusinessType.DELETE)
    public AjaxResult delete(@RequestParam("id") List<Integer> ids){
        return toAjax(pensionPaymentService.removeEntityBatchByIds(ids));
    }

    @PatchMapping("/merchant/changeStatus")
    public AjaxResult changeStatus(@RequestBody PensionPayment pensionPayment){
        if(pensionPayment.getId() == null || pensionPayment.getStatus() == null)
            return AjaxResult.success();
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,pensionPaymentService.changeStatus(pensionPayment));
    }
    @DeleteMapping("/merchant/refresh")
    public void refreshCache(){
        aliPayManager.refreshCache(getDeptId());
    }
}
