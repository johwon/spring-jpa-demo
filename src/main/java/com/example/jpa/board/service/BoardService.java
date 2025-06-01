package com.example.jpa.board.service;

import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.entity.BoardComment;
import com.example.jpa.board.entity.BoardType;
import com.example.jpa.board.model.*;

import java.util.List;

public interface BoardService {

    ServiceResult addBoard(BoardTypeInput boardTypeInput);

    ServiceResult updateBoard(Long id, BoardTypeInput boardTypeInput);

    ServiceResult deleteBoard(Long id);

    List<BoardType> getAllBoardType();

    ServiceResult setBoardTypeUsing(Long id, BoardTypeUsing boardTypeUsing);

    List<BoardTypeCount> getBoardTypeCount();

    ServiceResult setBoardTop(Long id, boolean topYn);

    ServiceResult setBoardPeriod(Long id, BoardPeriod boardPeriod);

    ServiceResult setBoardHits(Long id, String email);

    ServiceResult setBoardLike(Long id, String email);

    ServiceResult setBoardUnLike(Long id, String email);

    ServiceResult addBadReport(Long id, String email, BoardBadReportInput boardBadReportInput);

    List<BoardBadReport> getBadReportList();

    ServiceResult scrap(Long id, String email);

    ServiceResult deleteScrap(Long id, String email);

    ServiceResult addBookmark(Long id, String email);

    ServiceResult deleteBookmark(Long id, String email);

    List<Board> postList(String email);

    List<BoardComment> commentList(String email);
}
