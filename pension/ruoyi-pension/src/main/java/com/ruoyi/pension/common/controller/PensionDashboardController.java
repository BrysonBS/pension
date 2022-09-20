package com.ruoyi.pension.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.common.domain.vo.ExampleVo;
import com.ruoyi.pension.common.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class PensionDashboardController extends BaseController {
    @Autowired
    private ScreenService screenService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/order/amount")
    public AjaxResult orderAmount(){
        List<ExampleVo> list = screenService.getListOrderAmountCount(ExampleVo.builder().build());
        ObjectNode root = objectMapper.createObjectNode();
        if(!list.isEmpty()) {
            Map<String,String> map = list.stream().collect(Collectors.toMap(e -> e.getName().concat(e.getLabel()),ExampleVo::getValue));
            List<String> xAxis = list.stream().map(ExampleVo::getName).distinct().toList();
            ArrayNode series = root.putPOJO("xAxis",xAxis)
                    .putArray("series");
            list.stream()
                    .map(ExampleVo::getLabel)
                    .distinct()
                    .forEach(label ->{
                        series.add(series.objectNode()
                                .put("name",label)
                                .putPOJO("data",
                                        xAxis.stream().map(name -> map.getOrDefault(name.concat(label),"0")).toList())
                        );
                    });
        }
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,root);
    }
    @GetMapping("/nursing/count")
    public AjaxResult nursingCount(){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,screenService.getListNursingCount(ExampleVo.builder().build()));
    }
    @GetMapping("/order/status")
    public AjaxResult orderStatus(){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,screenService.getListStatusCount(ExampleVo.builder().build()));
    }
    @GetMapping("/order/day")
    public AjaxResult getListOrderOfDay(){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,screenService.getListOrderOfDay(ExampleVo.builder().build()));
    }
    @GetMapping("/device/latest")
    public AjaxResult getLatestNotice(ExampleVo exampleVo){
        return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,screenService.getListNotice(exampleVo));
    }
}
