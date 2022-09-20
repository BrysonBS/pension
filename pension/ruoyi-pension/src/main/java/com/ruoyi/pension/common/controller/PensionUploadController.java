package com.ruoyi.pension.common.controller;

import com.ruoyi.pension.common.api.OssManager;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.ruoyi.pension.common.service.PensionUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/pension/download")
public class
PensionUploadController {
    @Autowired
    private PensionUploadService pensionUploadService;
    @PostMapping
    public void download(PensionUpload pensionUpload, HttpServletResponse response){
        pensionUploadService.download(pensionUpload,response);
    }
}
