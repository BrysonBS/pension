package com.ruoyi.pension.owon.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.utils.StringUtils;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeVo {
    private List<String> tags;
    private String info;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime time;
    @JsonIgnore
    private Boolean enable;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime ts;
    private String ieee;
    private Integer heartRate;
    private Integer respiratoryRate;
}
