package com.ruoyi.pension.bioland.domain.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@ToString
public enum DeviceType {
    BLPRESSURE("01", "血压计"),
    BLGLUCOSE("02", "血糖仪"),
    BLOXYGEN("03", "血氧数据"),
    TPERTURE("04", "体温数据"),
    ECG("05", "心电数据"),
    URICACID("06", "尿酸数据"),
    CHOLESTEROL("07", "胆固醇数据");

    @EnumValue
    private String code;
    private String description;

    public static DeviceType getEnumByCode(String code){
        return Arrays.stream(DeviceType.values())
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElse(null);
    }
}
