package com.example.oauthjwt.global.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.example.oauthjwt.domain..*(..))")
    public void all() {
    }

    @Pointcut("execution(* com.example.oauthjwt.domain..*Controller.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* com.example.oauthjwt.domain..*Service.*(..))")
    public void service() {
    }

    @Around("all()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            log.info("Method Name : {}", joinPoint.getSignature().getName());
            log.info("class path : {}", joinPoint.getSignature().getDeclaringType());
            log.info("timeMs = {}", timeMs);
        }
    }

    // Pointcut에 의해 필터링된 경로로 들어오는 경우 메서드 호출 전에 적용
    @Before("controller() || service()")
    public void beforeLogic(JoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Method method = getMethod(joinPoint);
        log.info("[Request]");
        log.info("[{}] {}", request.getMethod(), URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8));

        log.info("Method Name : {}", method.getName());
        // 파라미터 받아오기
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) log.info("no parameter");
        else {
            for (Object arg : args) {
                if (arg != null) {
                    log.info("parameter type = {}", arg.getClass().getSimpleName());
                    log.info("parameter value = {}", arg);
                }
            }
        }
    }

    // Poincut에 의해 필터링된 경로로 들어오는 경우 메서드 리턴 후에 적용
    @AfterReturning(value = "controller() || service()", returning = "returnObj")
    public void afterLogic(Object returnObj) {
        log.info("[Response]");
        if (returnObj != null) {
            log.info("return type = {}", returnObj.getClass().getSimpleName());
            log.info("return value = {}", returnObj);
        } else {
            log.info("this method not exist return value");
        }
    }

    // JoinPoint로 메서드 정보 가져오기
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}