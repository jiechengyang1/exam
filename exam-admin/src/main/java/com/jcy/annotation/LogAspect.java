package com.jcy.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.jcy.utils.AnnotationUtils.LogAndInvokeTargetMethod;

@Aspect
@Component
public class LogAspect {

    @Around("execution(public * com.jcy.controller.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标方法的对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Class clazz = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(clazz);

        return LogAndInvokeTargetMethod(joinPoint, logger,
                String.format("执行了 -----> %s中的%s法", clazz.getName(), method.getName()),
                String.format("%s中的%s方法执行完毕", clazz.getName(), method.getName()));
    }
}
