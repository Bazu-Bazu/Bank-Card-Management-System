package com.example.bankcards.aop;

import jakarta.persistence.Entity;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Aspect
@Log4j2
public class LoggingAspect {

    @Before(
            "execution(public * com.example.bankcards.controller.*.*(..)) || " +
            "execution(public * com.example.bankcards.service.*.*(..)) || " +
            "execution(public * com.example.bankcards.security.*.*.*(..))"
    )
    public void before(JoinPoint joinPoint) {
        String methodName = getMethodName(joinPoint);
        String className = getClassName(joinPoint);
        Object[] args = joinPoint.getArgs();

        log.info("▶ {}.{}() called.", className, methodName);
    }

    @AfterReturning(
            pointcut =
                    "execution(public * com.example.bankcards.controller.*.*(..)) || " +
                    "execution(public * com.example.bankcards.service.*.*(..)) || " +
                    "execution(public * com.example.bankcards.security.*.*.*(..))",
            returning = "result"
    )
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String methodName = getMethodName(joinPoint);
        String className = getClassName(joinPoint);

        log.info("✅ {}.{}()", className, methodName);
    }

    @AfterThrowing(
            pointcut =
                    "execution(public * com.example.bankcards.controller.*.*(..)) || " +
                    "execution(public * com.example.bankcards.service.*.*(..)) || " +
                    "execution(public * com.example.bankcards.security.*.*.*(..))",
            throwing = "exception"
    )
    public void afterTrowing(JoinPoint joinPoint, Exception exception) {
        String methodName = getMethodName(joinPoint);
        String className = getClassName(joinPoint);

        log.warn("❌ {}.{}() threw exception: {} - {}",
                className, methodName, exception.getClass(), exception.getMessage());
    }

    private String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    private String getClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

}
