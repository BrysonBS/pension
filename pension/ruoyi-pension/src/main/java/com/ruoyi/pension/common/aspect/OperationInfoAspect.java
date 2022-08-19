package com.ruoyi.pension.common.aspect;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Aspect
@Slf4j
public class OperationInfoAspect {
    @Before("@annotation(operationInfo)")
    public void doBefore(JoinPoint point, OperationInfo operationInfo){
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null) return;
        SysUser currentUser = loginUser.getUser();
        if(currentUser == null) return;
        if(point.getArgs()[0] instanceof BasePensionEntity pensionEntity){
            if(operationInfo.value() == OperationInfo.Info.CREATED){
                pensionEntity.setCreateBy(currentUser.getUserName());
                pensionEntity.setCreateTime(LocalDateTime.now());
            }else if(operationInfo.value() == OperationInfo.Info.UPDATED){
                pensionEntity.setUpdateBy(currentUser.getUserName());
                pensionEntity.setUpdateTime(LocalDateTime.now());
            }
        }
    }
}
