package com.example.jpa.user.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.user.service.UserService;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApiUserInterestController {

    private final UserService userService;

    // 관심사용자 등록
    @PutMapping("/api/user/{id}/interest")
    public ResponseEntity<?> interestUser(@PathVariable Long id,
                                          @RequestHeader("F-TOKEN") String token){
        String email = "";
        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseEntity.badRequest().body("토큰이 유효하지 않습니다.");
        }

        ServiceResult result = userService.addInterestUser(id, email);
        return ResponseResult.result(result);

    }

    // 관심사용자 삭제
    @DeleteMapping("/api/user/interest/{id}")
    public ResponseEntity<?> deleteInterestUser(@PathVariable Long id,
                                          @RequestHeader("F-TOKEN") String token){
        String email = "";
        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseEntity.badRequest().body("토큰이 유효하지 않습니다.");
        }

        ServiceResult result = userService.deleteInterestUser(id, email);
        return ResponseResult.result(result);

    }

}
