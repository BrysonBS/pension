package com.ruoyi.pension.nursing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.common.domain.enums.TableEnum;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.ruoyi.pension.common.service.PensionUploadService;
import com.ruoyi.pension.nursing.domain.po.NursingWorker;
import com.ruoyi.pension.nursing.service.NursingWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/nursing/worker")
public class NursingWorkerController extends BaseController {
    @Autowired
    private NursingWorkerService nursingWorkerService;
    @Autowired
    private PensionUploadService pensionUploadService;
    @GetMapping("/list")
    public TableDataInfo get(NursingWorker nursingWorker){
        startPage();
        List<NursingWorker> list = nursingWorkerService.getListByExample(nursingWorker);
        return getDataTable(list);
    }
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Integer id){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingWorkerService.getById(id));
    }
    @PreAuthorize("@ss.hasPermi('nursing:worker:add')")
    @Log(title = "护理管理/护工", businessType = BusinessType.INSERT)
    @PostMapping
    @OperationInfo(OperationInfo.Info.CREATED)//添加创建人和创建时间
    public AjaxResult add(NursingWorker nursingWorker){
        if(nursingWorker.getId() != null)
            return AjaxResult.error("已存在");
        if(nursingWorker.getDeptId() == null)
            nursingWorker.setDeptId(getDeptId());
        return toAjax(nursingWorkerService.save(nursingWorker));
    }

    @PreAuthorize("@ss.hasPermi('nursing:worker:edit')")
    @Log(title = "护理管理/护工", businessType = BusinessType.UPDATE)
    @PutMapping()
    @OperationInfo(OperationInfo.Info.UPDATED)
    public AjaxResult update(@RequestBody NursingWorker nursingWorker){
        if(nursingWorker.getId() == null)
            return AjaxResult.error("不存在");
        return toAjax(nursingWorkerService.updateById(nursingWorker));
    }

    @PreAuthorize("@ss.hasPermi('nursing:worker:remove')")
    @Log(title = "护理管理/护工", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult delete(@RequestParam("id") List<Integer> ids){
        return toAjax(nursingWorkerService.removeBatchByIds(ids));
    }

    /** 获取证书列表 */
    @GetMapping("/{id}/certificate")
    public AjaxResult getCertificate(@PathVariable("id") Integer id){
        return AjaxResult
                .success()
                .put(AjaxResult.DATA_TAG,
                        pensionUploadService.getByTableEnumAndRelateIds(TableEnum.NURSING_WORKER_CERTIFICATE,List.of(id)));
    }
    /** 添加证书 */
    @Log(title = "护理管理/护工/证书", businessType = BusinessType.INSERT)
    @PostMapping(value = "/{id}/certificate",consumes = "multipart/form-data")
    public AjaxResult addCertificate(@PathVariable("id") Integer id,
                                     @RequestPart("certificate") MultipartFile certificate) throws IOException {
        String contentType = (contentType = certificate.getContentType()) == null ? "" : contentType;
        if(contentType.indexOf("image") != 0) return AjaxResult.error("仅支持图片类型!");
        PensionUpload pensionUpload = nursingWorkerService.addCertificate(id,certificate);
        return  pensionUpload != null ? AjaxResult.success().put(AjaxResult.DATA_TAG,pensionUpload) :
                AjaxResult.error("添加失败!");
    }
    /** 删除证书 */
    @Log(title = "护理管理/护工/证书", businessType = BusinessType.DELETE)
    @DeleteMapping("/certificate/{id}")
    public AjaxResult deleteCertificate(@PathVariable("id") Integer id){
        PensionUpload pensionUpload = pensionUploadService.getById(id);
        if(pensionUpload == null) return AjaxResult.error();
        return toAjax(pensionUploadService.removeEntity(pensionUpload));
    }
    /** 下载证书 */
    @Log(title = "护理管理/护工/证书", businessType = BusinessType.EXPORT)
    @PostMapping("/certificate/download/{id}")
    public void downloadCertificate(@PathVariable("id") Integer id, HttpServletResponse response){
        pensionUploadService.download(pensionUploadService.getById(id),response);
    }
}
