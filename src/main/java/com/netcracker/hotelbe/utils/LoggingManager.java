package com.netcracker.hotelbe.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Aspect
@Component
public class LoggingManager {

    private static final Logger LOG = LogManager.getLogger(LoggingManager.class);

    @Pointcut("execution(* com.netcracker.hotelbe.*.*(..) )")
    public void allPointcut() {

    }

    @Around("bean(*Controller)")
    public Object logControllerMethods(final ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().toString();
        Object object = pjp.proceed();

        if (className.contains("com.netcracker.hotelbe.controller")) {
            if (LOG.isInfoEnabled()) {
                LOG.info(className + " : " + methodName + "() " + "Response : "
                        + new ObjectMapper().writeValueAsString(object));
            }
        }

        return object;
    }

    @Around("bean(*Service)")
    public Object logServiceMethods(final ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().toString();
        Object object = pjp.proceed();

        if (className.contains("com.netcracker.hotelbe.service")) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(className + " : " + methodName + "() " + "Return : "
                        + new ObjectMapper().writeValueAsString(object));
            }
        }

        return object;
    }

    @AfterThrowing(value = "allPointcut()", throwing = "e")
    public void logAllThrowing(final JoinPoint joinPoint, final Throwable e) {
        Throwable rootCause = Throwables.getRootCause(e);
        if (rootCause instanceof PSQLException || rootCause instanceof EntityNotFoundException) {
            return;
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().toString();
        Object[] args = joinPoint.getArgs();
        StringBuilder arguments = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            arguments.append(i + 1).append(": ").append(args[i]).append(";\n\t");
        }
        if (LOG.getLevel() == Level.ERROR) {
            LOG.error(className + " : " + methodName + "() " + " arguments:\n\t"
                    + arguments, e);
        }
    }

}
