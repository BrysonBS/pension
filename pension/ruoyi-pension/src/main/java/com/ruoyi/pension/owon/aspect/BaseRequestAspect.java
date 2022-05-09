package com.ruoyi.pension.owon.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.owon.api.AccessToken;
import com.ruoyi.pension.owon.aspect.annotation.BaseRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Aspect
@Component
public class BaseRequestAspect {
    @Around("@annotation(baseRequest)")
    public void around(ProceedingJoinPoint joinPoint, BaseRequest baseRequest) throws ExecutionException, InterruptedException, JsonProcessingException {

        //Object[] args = joinPoint.getArgs();
        //return joinPoint.proceed(args);
    }
}
