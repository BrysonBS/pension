package com.ruoyi.pension.owon.domain.enums;

public enum Platform {
    NATIVE("本地"),
    OWON("欧万")
    ;
    private String name;
    Platform(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
