package com.example.jpa.board.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardType;
import com.example.jpa.board.model.*;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.board.service.BoardServiceImpl;
import com.example.jpa.common.exception.BizException;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.user.model.ResponseMessage;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.PushBuilder;
import javax.validation.Valid;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class ApiBoardController {

    private final BoardService boardService;

    // 게시판 타입 추가
    @PostMapping("/type")
    public ResponseEntity<?> addBoardType(@RequestBody @Valid BoardTypeInput boardTypeInput, Errors errors) {

        if (errors.hasErrors()) {
            List<ResponseError> responseErrors = ResponseError.of(errors.getAllErrors());

            return new ResponseEntity<>(ResponseMessage.fail("입력값이 정확하지 않습니다.", responseErrors), HttpStatus.BAD_REQUEST);
        }

        ServiceResult result = boardService.addBoard(boardTypeInput);

        if(!result.isResult()){
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/type/{id}")
    public ResponseEntity<?> updateBoardType(@PathVariable Long id, @RequestBody @Valid BoardTypeInput boardTypeInput, Errors errors) {

        if (errors.hasErrors()) {
            List<ResponseError> responseErrors = ResponseError.of(errors.getAllErrors());

            return new ResponseEntity<>(ResponseMessage.fail("입력값이 정확하지 않습니다.", responseErrors), HttpStatus.BAD_REQUEST);
        }

        ServiceResult result = boardService.updateBoard(id, boardTypeInput);

        if(!result.isResult()){
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/type/{id}")
    public ResponseEntity<?> deleteBoardType(@PathVariable Long id){
        ServiceResult result = boardService.deleteBoard(id);

        if(!result.isResult()){
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    // 게시판 타입 목록 조회
    @GetMapping("/type")
    public ResponseEntity<?> getBoardType(){

        List<BoardType> boardTypeList = boardService.getAllBoardType();

        return ResponseEntity.ok().body(ResponseMessage.success(boardTypeList));

    }

    // 게시판 타입 사용 여부 설정
    @PatchMapping("/type/{id}/using")
    public ResponseEntity<?> usingBoardType(@PathVariable Long id, @RequestBody BoardTypeUsing boardTypeUsing){
        ServiceResult result = boardService.setBoardTypeUsing(id, boardTypeUsing);

        if(!result.isResult()){
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    // 게시판 별 작성된 게시글의 개수(현재 사용 가능한 게시판에 한함)
    @GetMapping("/type/count")
    public ResponseEntity<?> boardTypeCount(){

        List<BoardTypeCount> boardTypeCountList = boardService.getBoardTypeCount();

        return ResponseEntity.ok().body(boardTypeCountList);
    }

    // 게시글 최상단 배치
    @PatchMapping("/{id}/top")
    public ResponseEntity<?> boardPostTop(@PathVariable Long id){

        ServiceResult result = boardService.setBoardTop(id, true);
        return ResponseEntity.ok().body(result);

    }

    // 게시글 최상단 해지
    @PatchMapping("/{id}/top/clear")
    public ResponseEntity<?> boardPostTopClear(@PathVariable Long id){

        ServiceResult result = boardService.setBoardTop(id, false);
        return ResponseEntity.ok().body(result);

    }

    // 게시글의 게시기간을 시작일과 종료일로 설정
    @PatchMapping("/{id}/publish")
    public ResponseEntity<?> boardPeriod(@PathVariable Long id, @RequestBody BoardPeriod boardPeriod){

        ServiceResult result =boardService.setBoardPeriod(id, boardPeriod);

        if(!result.isResult()){
           return ResponseResult.fail(result.getMessage());
        }
        return ResponseResult.success();

    }

    // 게시글 조회수 증가
    @PutMapping("/{id}/hits")
    public ResponseEntity<?> boardHits(@PathVariable Long id, @RequestHeader("F-TOKEN") String token){

        String email = "";

        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        ServiceResult result = boardService.setBoardHits(id, email);
        if(result.isFail()){
            return ResponseResult.fail(result.getMessage());
        }
        return ResponseResult.success();

    }

    // 게시글 좋아요 증가
    @PutMapping("/{id}/like")
    public ResponseEntity<?> boardLike(@PathVariable Long id, @RequestHeader("F-TOKEN") String token){
        String email = "";

        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        ServiceResult result = boardService.setBoardLike(id, email);

        return ResponseResult.result(result);

    }

    // 좋아요 취소
    @PutMapping("/{id}/unlike")
    public ResponseEntity<?> boardUnLike(@PathVariable Long id, @RequestHeader("F-TOKEN") String token){
        String email = "";

        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        ServiceResult result = boardService.setBoardUnLike(id, email);

        return ResponseResult.result(result);

    }

    // 게시글 신고
    @PutMapping("{id}/badreport")
    public ResponseEntity<?> boardBadReport(@PathVariable Long id,
                                            @RequestHeader("F-TOKEN") String token,
                                            @RequestBody BoardBadReportInput boardBadReportInput){
        String email = "";

        try{
            email = JWTUtils.getIssuer(token);
        }catch (JWTVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        ServiceResult result = boardService.addBadReport(id, email, boardBadReportInput);

        return ResponseResult.result(result);
    }

    // 게시글 상세조회 시 로그 남기기
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id){

        Board board = null;
        try{
            board = boardService.detail(id);
        }catch (BizException e){
            return ResponseResult.fail(e.getMessage());
        }

        return ResponseResult.success(board);



    }


}



















