package com.ruoyi.pension.common.controller;

import com.ruoyi.pension.common.api.OssManager;
import com.ruoyi.pension.common.domain.po.PensionUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/pension/download")
public class PensionUploadController {
    @Autowired
    private OssManager ossManager;
    @PostMapping
    public void download(PensionUpload uploadInfo, HttpServletResponse response){
        ossManager.downloadByStream(uploadInfo.getRootPath() + uploadInfo.getUri(),uploadInfo.getFileName(),response);
    }
}
