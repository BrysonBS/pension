package com.ruoyi.pension.nursing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.nursing.domain.po.NursingPerson;
import com.ruoyi.pension.nursing.service.NursingPersonService;
import org.checkerframework.checker.guieffect.qual.PolyUIType;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/{id}")
    public AjaxResult getPerson(@PathVariable("id") Integer id){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,nursingPersonService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('nursing:person:add')")
    @Log(title = "护理管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(NursingPerson nursingPerson){
        if(nursingPerson.getId() != null)
            return AjaxResult.error("已存在,无法新增");
        LoginUser loginUser = getLoginUser();
        nursingPerson.setCreateBy(loginUser.getUsername());
        nursingPerson.setCreateTime(LocalDateTime.now());
        nursingPerson.setDeptId(loginUser.getDeptId());
        return toAjax(nursingPersonService.save(nursingPerson));
    }

    @PreAuthorize("@ss.hasPermi('nursing:person:edit')")
    @Log(title = "护理管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody NursingPerson nursingPerson){
        if(nursingPerson.getId() == null)
            return AjaxResult.error("不存在,无法更新");
        nursingPerson.setUpdateBy(getLoginUser().getUsername());
        nursingPerson.setUpdateTime(LocalDateTime.now());
        return toAjax(nursingPersonService.updateById(nursingPerson));
    }

    @PreAuthorize("@ss.hasPermi('nursing:person:remove')")
    @Log(title = "护理管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult delete(@RequestParam("id") List<Integer> ids){
        return toAjax(nursingPersonService.removeBatchByIds(ids));
    }
}
