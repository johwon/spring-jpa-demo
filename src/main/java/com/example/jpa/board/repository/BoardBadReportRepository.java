package com.example.jpa.board.repository;

import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.entity.BoardHits;
import com.example.jpa.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardBadReportRepository extends JpaRepository<BoardBadReport, Long> {

}
