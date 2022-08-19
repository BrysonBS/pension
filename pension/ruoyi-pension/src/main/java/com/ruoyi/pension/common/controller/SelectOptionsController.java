package com.ruoyi.pension.common.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.common.domain.vo.SelectOptionVo;
import com.ruoyi.pension.common.service.SelectOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/select/options")
public class SelectOptionsController {
    @Autowired
    private SelectOptionService selectOptionService;
    @GetMapping("/user")
    public AjaxResult getUserOptions(@RequestParam(required = false) Map<String, String> params){
        return AjaxResult.success().put(AjaxResult.DATA_TAG,
                selectOptionService.getUserOptions(
                        SelectOptionVo.<Long,Long>builder()
                                .params(params)
                                .build()
                ));
    }
}
