
package com.fraudwatch.fraudruleengine.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        log.info("Request: {}.{}({}) - URI: {}, Method: {}, IP: {}",
                className,
                methodName,
                formatArgs(args, parameterNames),
                request != null ? request.getRequestURI() : "N/A",
                request != null ? request.getMethod() : "N/A",
                request != null ? request.getRemoteAddr() : "N/A");

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("Response: {}.{} - Time: {}ms", className, methodName, (end - start));

        return result;
    }

    private String formatArgs(Object[] args, String[] parameterNames) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            String name = (parameterNames != null && i < parameterNames.length) ? parameterNames[i] : "arg" + i;
            Object value = args[i];
            if (value instanceof String && ((String) value).length() > 100) {
                value = ((String) value).substring(0, 100) + "...";
            }
            sb.append(name).append("=").append(value);
        }
        return sb.toString();
    }
}

