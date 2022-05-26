package com.ruoyi.pension.owon.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum Platform {
    NATIVE(1,"本地"),
    OWON(2,"欧万"),
    BIOLAND(3,"爱奥乐")
    ;
    @EnumValue
    private final Integer code;
    private final String name;
}
