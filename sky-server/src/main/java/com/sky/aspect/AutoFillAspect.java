package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

//自定义切面类
@Component
@Aspect
@Slf4j
public class AutoFillAspect {

    /**
     * 切点定义，抽离切入点表达式，解耦合
     * 也可直接写   @Before("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    void AutoFillPoint(){};

    @Before("AutoFillPoint()")
    void AutoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("公共字段自动填充....");
        //获取当前被拦截对象的方法上的数据库操作对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        //获取当前被拦截的方法的参数--这里是方法里面的实体对象
        Object[] args = joinPoint.getArgs();
        Object entity = args[0];

        //准备需要填充的数据
        LocalDateTime time = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        //根据不同的操作类型填充数据
        Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
        Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
        if (operationType == OperationType.INSERT){
            Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
            Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
            setCreateTime.invoke(entity,time);
            setCreateUser.invoke(entity,id);
            setUpdateTime.invoke(entity,time);
            setUpdateUser.invoke(entity,id);
        } else if (operationType == OperationType.UPDATE) {
            setUpdateTime.invoke(entity,time);
            setUpdateUser.invoke(entity,id);
        }

    }
}
