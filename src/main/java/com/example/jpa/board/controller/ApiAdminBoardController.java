package com.example.jpa.board.controller;

import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.model.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/board")
public class ApiAdminBoardController {

    private final BoardService boardService;

    @GetMapping("/badreport")
    public ResponseEntity<?> badReport(){

        List<BoardBadReport> list = boardService.getBadReportList();
        return ResponseResult.success(list);
    }
}
