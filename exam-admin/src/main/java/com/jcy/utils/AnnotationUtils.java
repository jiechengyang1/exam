package com.jcy.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

public class AnnotationUtils {

    public static Object LogAndInvokeTargetMethod(ProceedingJoinPoint joinPoint, Logger logger, String startLogMsg, String endLogMsg) throws Throwable {
        long startTime = System.currentTimeMillis();
        logger.info(startLogMsg);
        // 执行目标方法
        Object result = joinPoint.proceed(joinPoint.getArgs());
        long endTime = System.currentTimeMillis();
        logger.info(endLogMsg + ", 耗时: {}ms", endTime - startTime);
        return result;
    }

    public static String parseSpel(String key, Method method, Object[] args) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }

        // 获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = localVariableTableParameterNameDiscoverer.getParameterNames(method);

        // 使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        // SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 把方法参数放入SPEL上下文中
        for (int i = 0; i < Objects.requireNonNull(paraNameArr).length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }

}
