package com.ruoyi.pension.common.aspect.annotation;

import java.lang.annotation.*;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationInfo {
    /**
     * 创建还是修改操作
     * @return
     */
    Info value();
    enum Info{
        CREATED,
        UPDATED
        ;
        Info() {}
    }
}
