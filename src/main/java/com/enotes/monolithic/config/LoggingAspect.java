package com.enotes.monolithic.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.enotes.monolithic.service.impl..*(..)) || execution(* com.enotes.monolithic.controller..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        logger.debug("Entering: {}.{}() with arguments: {}", className, methodName, Arrays.toString(args));
        
        try {
            Object result = joinPoint.proceed();
            logger.debug("Exiting: {}.{}() with result: {}", className, methodName, result);
            return result;
        } catch (Exception e) {
            logger.error("Exception in {}.{}() with cause: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}
