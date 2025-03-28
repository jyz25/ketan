package com.ketan.core.mdc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * MDC 通常指的是 Mapped Diagnostic Context（映射诊断上下文），
 * 它是一种用于在日志记录中跟踪请求或事务的上下文信息的机制。
 */
@Slf4j
@Aspect
@Component
public class MdcAspect implements ApplicationContextAware {
    private ExpressionParser parser = new SpelExpressionParser();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(MdcDot) || @within(MdcDot)")
    public void getLogAnnotation() {
    }

    /**
     * handle 方法是一个环绕通知，它在被拦截的方法执行前后执行。
     * 这个方法首先记录开始时间，然后尝试添加 MDC 信息（通过调用 addMdcCode 方法），
     * 接着执行被拦截的方法，并在最后记录执行耗时并清理 MDC 信息（如果之前添加了的话）。
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("getLogAnnotation()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        boolean hasTag = addMdcCode(joinPoint);
        try {
            Object ans = joinPoint.proceed();
            return ans;
        } finally {
            log.info("执行耗时: {}#{} = {}ms",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(), // 简单类名
                    joinPoint.getSignature().getName(), // 方法名
                    System.currentTimeMillis() - start);
            if (hasTag) {
                MdcUtil.reset();
            }
        }
    }

    private boolean addMdcCode(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 在方法上查找该注解
        MdcDot dot = method.getAnnotation(MdcDot.class);
        if (dot == null) {
            // 方法上没有该注解，则在类上查找是否有该注解
            dot = (MdcDot) joinPoint.getSignature().getDeclaringType().getAnnotation(MdcDot.class);
        }

        if (dot != null) {
            MdcUtil.add("bizCode", loadBizCode(dot.bizCode(), joinPoint));
            return true;
        }
        return false;
    }

    private String loadBizCode(String key, ProceedingJoinPoint joinPoint) {
        if (StringUtils.isBlank(key)) {
            return "";
        }

        StandardEvaluationContext context = new StandardEvaluationContext();

        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        String[] params = parameterNameDiscoverer.getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
