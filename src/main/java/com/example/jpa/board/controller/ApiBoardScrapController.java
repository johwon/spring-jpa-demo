package com.example.jpa.board.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApiBoardScrapController {

    private final BoardService boardService;

    // 게시글 스크랩 추가
    @PutMapping("/api/board/{id}/scrap")
    public ResponseEntity<?> boardScrap(@PathVariable Long id,
                                        @RequestHeader("F-TOKEN") String token){
        String email = "";
        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        return ResponseResult.result(boardService.scrap(id, email));

    }

    // 게시글 스크랩 취소
    @DeleteMapping("/api/scrap/{id}")
    public ResponseEntity<?> deleteBoardScrap(@PathVariable Long id,
                                        @RequestHeader("F-TOKEN") String token) {
        String email = "";
        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        return ResponseResult.result(boardService.deleteScrap(id, email));

    }

}
