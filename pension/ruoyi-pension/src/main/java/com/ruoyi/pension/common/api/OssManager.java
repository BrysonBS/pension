package com.ruoyi.pension.common.api;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.ruoyi.framework.config.properties.AliyunProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class OssManager {
    @Autowired
    private AliyunProps aliyunProps;

    /**
     * 创建OSSClient实例
     * @return
     */
    public OSS getOSSClient(){
        return new OSSClientBuilder()
                .build(aliyunProps.getOssEndpoint(), aliyunProps.getAccessKey(), aliyunProps.getSecret());
    }

    /**
     * 流式上传文件到OSS
     * @param uri OSS中文件的相对路径
     * @param input 文件流
     * @return
     */
    public boolean uploadByStream(String uri, InputStream input){
        OSS ossClient = getOSSClient();
        boolean result = false;
        try {
            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunProps.getBucket(), uri, input);
            //上传
            ossClient.putObject(putObjectRequest);
            result = true;
        } catch (OSSException oe) {
            String message = String.format("oss流式上传失败错误码:%s,Request ID:%s,Host ID:%s,错误信息: %s",
                    oe.getErrorCode(),oe.getRequestId(),oe.getHostId(),oe.getErrorMessage());
            log.error(message);
            System.out.println(message);
        } catch (ClientException ce) {
            log.error("oss流式上传失败错误信息: {}", ce.getMessage());
            System.out.println("oss流式上传失败错误信息:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return result;
    }

    /**
     * 流式文件下载
     * @param uri OSS中文件的相对路径
     * @param response 获取文件后响应给客户端
     */
    public void downloadByStream(String uri,String fileName, HttpServletResponse response){
        // 创建OSSClient实例。
        OSS ossClient = getOSSClient();
        try {
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流
            OSSObject ossObject = ossClient.getObject(aliyunProps.getBucket(), uri);
            long fileSize = ossObject.getObjectMetadata().getContentLength();
            // 读取文件内容
            InputStream input = ossObject.getObjectContent();
            //响应设置
            response.reset();
            //响应类型
            response.setContentType("application/octet-stream; charset=UTF-8");
            //文件长度
            response.setHeader("Content-Length", String.valueOf(fileSize));
            //编码格式
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            OutputStream output = response.getOutputStream();
            try(ossObject;input;output) {
                input.transferTo(response.getOutputStream());
            }
        } catch (OSSException oe) {
            String message = String.format("oss流式下载失败错误码:%s,Request ID:%s,Host ID:%s,错误信息: %s",
                    oe.getErrorCode(),oe.getRequestId(),oe.getHostId(),oe.getErrorMessage());
            log.error(message);
            System.out.println(message);
        } catch (Throwable ce) {
            log.error("oss流式下载失败错误信息: {}", ce.getMessage());
            System.out.println("oss流式下载失败错误信息:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
