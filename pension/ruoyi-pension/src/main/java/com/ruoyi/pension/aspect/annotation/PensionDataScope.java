package com.ruoyi.pension.aspect.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)//作用与方法
@Retention(RetentionPolicy.RUNTIME) //生命周期:运行时可获取
@Documented
public @interface PensionDataScope {
    /**
     * 包含dept_id的表的别名
     */
    public String deptAlias() default "";
    /**
     * 包含user_id的表的别名
     */
    public String userAlias() default "";
}
