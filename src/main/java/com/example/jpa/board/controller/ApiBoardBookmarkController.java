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
public class ApiBoardBookmarkController {

    private final BoardService boardService;

    // 게시글 북마크 추가
    @PutMapping("/api/board/{id}/bookmark")
    public ResponseEntity<?> boardBookmark(@PathVariable Long id,
                                        @RequestHeader("F-TOKEN") String token){
        String email = "";
        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        return ResponseResult.result(boardService.addBookmark(id, email));

    }

    // 게시글 북마크 삭제
    @DeleteMapping("/api/bookmark/{id}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id,
                                        @RequestHeader("F-TOKEN") String token) {
        String email = "";
        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        return ResponseResult.result(boardService.deleteBookmark(id, email));

    }
    
}
