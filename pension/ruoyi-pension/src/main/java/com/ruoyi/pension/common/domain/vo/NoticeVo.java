package com.ruoyi.pension.common.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.common.domain.enums.Operation;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeVo {
    private String type; //类型
    private String payType;//支付方式
    private String tradeState;//交易状态
    private String outTradeNo;//订单号
    private String tradeType;//交易类型
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
