package com.ruoyi.pension.nursing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.nursing.domain.po.NursingServicePrice;
import com.ruoyi.pension.nursing.service.NursingServicePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/nursing/service/price")
public class NursingServicePriceController extends BaseController {
    @Autowired
    private NursingServicePriceService nursingServicePriceService;
    @GetMapping("/list")
    public TableDataInfo getListByExample(NursingServicePrice nursingServicePrice){
        startPage();
        List<NursingServicePrice> list = nursingServicePriceService.getListByExample(nursingServicePrice);
        return getDataTable(list);
    }
    @GetMapping("/listOwn")
    public AjaxResult getListOwn(){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingServicePriceService.getListOrAncestorByDeptId(getDeptId()));
    }

    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Integer id){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingServicePriceService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('nursing:service:add')")
    @Log(title = "护理管理/护理项目", businessType = BusinessType.INSERT)
    @PostMapping
    @OperationInfo(OperationInfo.Info.CREATED)//添加创建人和创建时间
    public AjaxResult post(@RequestBody @Valid NursingServicePrice nursingServicePrice){
        if(nursingServicePrice.getId() != null) return AjaxResult.error("已存在,无法新增");
        if(nursingServicePrice.getDeptId() == null) nursingServicePrice.setDeptId(getDeptId());
        nursingServicePrice.setUserId(getUserId());
        return toAjax(nursingServicePriceService.save(nursingServicePrice));
    }

    @PreAuthorize("@ss.hasPermi('nursing:person:edit')")
    @Log(title = "护理管理/护理项目", businessType = BusinessType.UPDATE)
    @OperationInfo(OperationInfo.Info.UPDATED)//添加创建人和创建时间
    @PutMapping
    public AjaxResult update(@RequestBody NursingServicePrice nursingServicePrice){
        if(nursingServicePrice.getId() == null)
            return AjaxResult.error("不存在,无法更新");
        return toAjax(nursingServicePriceService.updateById(nursingServicePrice));
    }

    @PreAuthorize("@ss.hasPermi('nursing:service:remove')")
    @Log(title = "护理管理/护理项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult batch(@RequestParam("id") List<Integer> ids){
        return toAjax(nursingServicePriceService.removeBatchByIds(ids));
    }
}
