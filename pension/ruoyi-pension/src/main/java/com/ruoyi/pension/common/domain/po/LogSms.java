package com.ruoyi.pension.common.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName log_sms
 */
@TableName(value ="log_sms")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogSms implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String phones;
    private String content;
    private String templateCode;
    private LocalDateTime sendTime;
    @JsonProperty("RequestId")
    private String requestId;
    @JsonProperty("Message")
    private String resultMessage;
    @JsonProperty("BizId")
    private String resultBizid;
    @JsonProperty("Code")
    private String resultCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}