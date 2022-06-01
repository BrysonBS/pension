package com.ruoyi.pension.owon.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.pension.common.domain.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName owon_notice
 */
@TableName(value ="owon_notice")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwonNotice implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer reportId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime reportTime;
    private String deviceIeee;
    private String deviceName;
    private String noticeType;
    private String noticeContent;
    private String description;
    private LocalDateTime createTime;
    @TableLogic(value = "0",delval = "2")
    private String delFlag;
    private String displayName;
    private Platform source;
    private Long deptId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}