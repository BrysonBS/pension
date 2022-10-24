package com.ruoyi.pension.common.aspect;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import com.ruoyi.system.service.ISysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@Aspect
@Slf4j
public class PensionDataScopeAspect {
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Autowired
    private ISysDeptService iSysDeptService;

    @Before("@annotation(pensionDataScope)")
    public void doBefore(JoinPoint point, PensionDataScope pensionDataScope){
        try {
            //存在则先清空
            consumerDataScope(point,map -> map.put(DATA_SCOPE, ""));
            handleDataScope(point,pensionDataScope);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //先清空参数
    @SuppressWarnings("unchecked")
    private void consumerDataScope(final JoinPoint point, Consumer<Map<String,Object>> consumer) throws NoSuchFieldException, IllegalAccessException {
        Object obj = point.getArgs()[0];
        if(obj instanceof BasePensionEntity entity){
            if(entity.getParams() == null) entity.setParams(new HashMap<>());
            consumer.accept(entity.getParams());
            return;
        }
        //没有继承则使用反射
        Class<?> clazz = obj.getClass();
        Field field = clazz.getDeclaredField("params");
        field.setAccessible(true);
        if(field.get(obj) == null){
            HashMap<String,Object> hashMap = new HashMap<>();
            field.set(obj,hashMap);
        }
        if(field.get(obj) instanceof Map<?,?> params){
            Map<String,Object> map = (Map<String, Object>) params;
            consumer.accept(map);
        }
    }
    private LoginUser getLoginUser(final JoinPoint point) throws IllegalAccessException {
        Object obj = point.getArgs()[0];
        if(obj instanceof BasePensionEntity entity){
            if(entity.getLoginUser() != null)
                return entity.getLoginUser();
            return SecurityUtils.getLoginUser();
        }
        //没有继承则使用反射
        Class<?> clazz = obj.getClass();
        Field field = null;
        try {
            field = clazz.getDeclaredField("loginUser");
        } catch (NoSuchFieldException e) {
            //不存在字段直接返回
            return SecurityUtils.getLoginUser();
        }
        field.setAccessible(true);
        if(field.get(obj) instanceof LoginUser loginUser) return loginUser;
        return SecurityUtils.getLoginUser();
    }
    private void handleDataScope(JoinPoint point,PensionDataScope pensionDataScope) throws NoSuchFieldException, IllegalAccessException {
        // 获取当前的用户
        LoginUser loginUser = getLoginUser(point);
        if(loginUser == null) return;

        String deptAlias = StringUtils.isBlank(deptAlias = pensionDataScope.deptAlias()) ? "" : deptAlias + ".";
        String userAlias = StringUtils.isBlank(userAlias = pensionDataScope.userAlias()) ? "" : userAlias + ".";
        StringBuilder builder = new StringBuilder();
        boolean ignoreDept = hasDeptId(point,builder,deptAlias,loginUser.getUser()) ||
                pensionDataScope.ignoreDept();//忽略部门权限过滤
        boolean ignoreUser = pensionDataScope.ignoreUser();//忽略用户权限过滤

        SysUser currentUser = loginUser.getUser();
        if(currentUser != null && !currentUser.isAdmin()) {
            //非管理员用户则过滤
            for (SysRole role : currentUser.getRoles()) {
                String dataScope = role.getDataScope();
                if (DATA_SCOPE_ALL.equals(dataScope)) return; //所有权限
                if (!ignoreDept) {
                    if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                        builder.append(StringUtils.format(
                                " OR {}dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias,
                                role.getRoleId()));
                    } else if (DATA_SCOPE_DEPT.equals(dataScope)) {
                        builder.append(StringUtils.format(" OR {}dept_id = {} ", deptAlias, currentUser.getDeptId()));
                    } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                        builder.append(StringUtils.format(
                                " OR {}dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) ) ",
                                deptAlias, currentUser.getDeptId(), currentUser.getDeptId()));
                    }
                }
                if (!ignoreUser && DATA_SCOPE_SELF.equals(dataScope)) {
                    builder.append(StringUtils.format(" OR {}user_id = {} ", userAlias, currentUser.getUserId()));
                }
            }
        }

        if(StringUtils.isBlank(builder.toString())) return;

        //设置值
        consumerDataScope(point,map ->
            map.put(DATA_SCOPE, " AND (" + builder.substring(4) + ") ")
        );
    }

    private boolean hasDeptId(JoinPoint point,StringBuilder builder,String deptAlias,SysUser currentUser) throws IllegalAccessException {
       Object obj = point.getArgs()[0];
       Class<?> clazz = obj.getClass();
        try {
            //获取deptId
            Field deptIdField = clazz.getDeclaredField("deptId");
            deptIdField.setAccessible(true);
            if(deptIdField.get(obj) instanceof Long deptId){
                SysDept sysDept = iSysDeptService.selectDeptById(deptId);
                if(sysDept.getDeptId().longValue() == deptId.longValue() || sysDept.getAncestors().contains(deptId.toString())){
                    builder.append(String.format(" OR %sdept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = %d or find_in_set( %d , ancestors ) )",
                            deptAlias,deptId,deptId));
                    return true;
                }
            }
        } catch (NoSuchFieldException ignored) {}
        return false;
    }
}
