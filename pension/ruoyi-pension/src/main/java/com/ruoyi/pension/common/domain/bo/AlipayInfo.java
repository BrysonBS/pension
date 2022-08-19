package com.ruoyi.pension.common.domain.bo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AlipayInfo {
    private Long userId;
    private Long deptId;
    private String tradeNo;
    private String subject;
    private BigDecimal totalAmount;
}
