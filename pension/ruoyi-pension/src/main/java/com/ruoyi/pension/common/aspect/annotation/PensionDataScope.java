package com.ruoyi.pension.common.aspect.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)//作用与方法
@Retention(RetentionPolicy.RUNTIME) //生命周期:运行时可获取
@Documented
public @interface PensionDataScope {
    /**
     * 包含dept_id的表的别名
     */
    String deptAlias() default "";
    /**
     * 包含user_id的表的别名
     */
    String userAlias() default "";

    /**
     * 忽略部门相关权限: 自定义数据权限/部门/部门及以下数据权限
     */
    boolean ignoreDept() default false;

    /**
     * 忽略用户相关权限: 仅本人数据权限
     */
    boolean ignoreUser() default false;
}
