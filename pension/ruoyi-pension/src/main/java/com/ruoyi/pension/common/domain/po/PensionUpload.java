package com.ruoyi.pension.common.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @TableName pension_upload
 */
@TableName(value ="pension_upload")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensionUpload implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer masterId;
    private String masterName;
    private Integer relateId;
    private String rootPath;
    private String uri;
    private String fileName;
    private String fileType;
    private Long fileSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime uploadTime;
    @TableLogic(value = "0",delval = "2")
    private String delFlag;

    @TableField(exist = false)
    private MultipartFile att;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}