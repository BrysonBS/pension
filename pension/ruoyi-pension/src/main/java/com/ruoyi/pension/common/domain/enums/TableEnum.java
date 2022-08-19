package com.ruoyi.pension.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TableEnum {
    NURSING_RECORD(1,"nursing_record","护理记录附件"),
    NURSING_WORKER_CERTIFICATE(2,"nursing_worker","护工证书")
    ;
    private final Integer code;
    private final String name;
    private final String description;
}
