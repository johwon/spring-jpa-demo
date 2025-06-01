package com.example.jpa.common.aop;

import com.example.jpa.board.service.BoardService;
import com.example.jpa.logs.service.LogsService;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.UserLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class BoardLogger {

    private final BoardService boardService;
    private final LogsService logsService;

    @Around("execution(* com.example.jpa..*.*Controller.detail(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable{

        log.info("############");
        log.info("############");
        log.info("컨트롤러 디테일 호출 전!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        Object result = joinPoint.proceed();

        if(joinPoint.getSignature().getDeclaringTypeName().contains("ApiBoardController")
            && "detail".equals(joinPoint.getSignature().getName())){

            StringBuilder sb = new StringBuilder();

            sb.append("파라미터:");
            Object[] args = joinPoint.getArgs();
            for(Object x : args){
                sb.append(x.toString());
            }

            sb.append("\n");
            sb.append("결과:");
            sb.append(result.toString());

            logsService.add(sb.toString());
            log.info(sb.toString());

        }

        log.info("############");
        log.info("############");
        log.info("컨트롤러 디테일 호출 후!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        return result;

    }
}
