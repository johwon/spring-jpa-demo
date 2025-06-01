package com.example.jpa.user.controller;

import com.example.jpa.common.exception.BizException;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.UserLogin;
import com.example.jpa.user.model.UserLoginToken;
import com.example.jpa.user.service.UserService;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiLoginController {

    private final UserService userService;

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLogin userLogin, Errors errors){

        log.info("로그인 함수!");
        if(errors.hasErrors()){
            return ResponseResult.fail("입력값이 정확하지 않습니다.", ResponseError.of(errors.getAllErrors()));
        }

        User user = null;
        try{
            user = userService.login(userLogin);
        }catch (BizException e){
            log.info("로그인 에러:" + e.getMessage());
            return ResponseResult.fail(e.getMessage());
        }
        UserLoginToken userLoginToken = JWTUtils.createToken(user);

        if(userLoginToken == null){
            log.info("JWT 생성 에러");
            return ResponseResult.fail("토큰 생성에 실패했습니다.");
        }

        return ResponseResult.success(userLoginToken);
    }
}
