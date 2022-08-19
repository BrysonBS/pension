package com.ruoyi.pension.nursing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.nursing.domain.po.NursingPerson;
import com.ruoyi.pension.nursing.service.NursingPersonService;
import org.checkerframework.checker.guieffect.qual.PolyUIType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/nursing/person")
public class NursingPersonController extends BaseController {
    @Autowired
    private NursingPersonService nursingPersonService;
    @GetMapping("/list")
    public TableDataInfo getListByExample(NursingPerson nursingPerson){
        startPage();
        List<NursingPerson> list = nursingPersonService.getListByExample(nursingPerson);
        return getDataTable(list);
    }
    @GetMapping("/listAll")
    public AjaxResult getListAll(){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingPersonService.getListPerson(NursingPerson.builder().build()));
    }
    @GetMapping("/listOwn")
    public  AjaxResult getListOwn(){
        //管理员则指定为-1
        Long userId = getLoginUser().getUser().getRoles().stream()
                .anyMatch(e -> e.getRoleKey().contains("admin")) ? -1L : getUserId();
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingPersonService.getListOwnByUserId(
                        NursingPerson.builder()
                                .userId(userId)
                                .build()));
    }
    @GetMapping("/{id}")
    public AjaxResult getPerson(@PathVariable("id") Integer id){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingPersonService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('nursing:person:add')")
    @Log(title = "护理管理/老人", businessType = BusinessType.INSERT)
    @OperationInfo(OperationInfo.Info.CREATED)
    @PostMapping
    public AjaxResult add(NursingPerson nursingPerson){
        if(nursingPerson.getId() != null)
            return AjaxResult.error("已存在,无法新增");
        if(nursingPerson.getDeptId() == null)
            nursingPerson.setDeptId(getDeptId());
        return toAjax(nursingPersonService.save(nursingPerson));
    }

    @PreAuthorize("@ss.hasPermi('nursing:person:edit')")
    @Log(title = "护理管理/老人", businessType = BusinessType.UPDATE)
    @OperationInfo(OperationInfo.Info.UPDATED)
    @PutMapping
    public AjaxResult update(@RequestBody NursingPerson nursingPerson){
        if(nursingPerson.getId() == null)
            return AjaxResult.error("不存在,无法更新");
        return toAjax(nursingPersonService.updateById(nursingPerson));
    }

    @PreAuthorize("@ss.hasPermi('nursing:person:remove')")
    @Log(title = "护理管理/老人", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult delete(@RequestParam("id") List<Integer> ids){
        return toAjax(nursingPersonService.removeBatchByIds(ids));
    }
}
