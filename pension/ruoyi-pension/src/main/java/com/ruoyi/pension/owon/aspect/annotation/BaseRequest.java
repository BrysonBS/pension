package com.ruoyi.pension.owon.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//作用与方法
@Retention(RetentionPolicy.RUNTIME) //生命周期:运行时可获取
public @interface BaseRequest {
}
