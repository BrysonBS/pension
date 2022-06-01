package com.ruoyi.pension.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TableEnum {
    NURSING_RECORD(1,"nursing_record");
    private final Integer code;
    private final String name;
}
