package com.example.jpa.common.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.jpa.common.exception.AuthFailException;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.util.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("##########################");
        log.info("[인터셉터] - preHandler start");
        log.info("##########################");
        log.info(request.getMethod());
        log.info(request.getRequestURI());

        if(!validJWT(request)){
            throw new AuthFailException("토큰이 유효하지 않습니다.");
        }

        return true;
    }

    private boolean validJWT(HttpServletRequest request) {

        String token = request.getHeader("F-TOKEN");

        String email = "";
        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return false;
        }
        return true;
    }

}
