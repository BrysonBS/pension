package com.ruoyi.pension.common.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.pension.common.domain.enums.Operation;
import lombok.*;

import java.time.LocalDateTime;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime ts;
    private String ieee;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer hbp; //高压
    private Integer lbp; //低压
    private Integer glucose; //血糖

    @JsonIgnore
    private Boolean enable;
    @JsonIgnore
    private Operation operation; //操作
}
