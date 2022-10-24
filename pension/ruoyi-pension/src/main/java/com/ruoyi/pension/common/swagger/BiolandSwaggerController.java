package com.ruoyi.pension.common.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Tag(name = "爱奥乐相关接口API")
@RestController
@RequestMapping("/swagger/bioland")
public class BiolandSwaggerController {
    @Operation(summary = "爱奥乐数据上报",security = { @SecurityRequirement(name = "Authorization") })
    @Parameter(name = "data",description = "上报数据",required = true)
    @GetMapping("/data")
    public void receiveData(HttpServletRequest request, HttpServletResponse response,@RequestParam("data") String data) throws Exception {
        request.getRequestDispatcher("/bioland/data").forward(request,response);
    }
}
