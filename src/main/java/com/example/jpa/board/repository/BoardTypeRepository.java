package com.example.jpa.board.repository;


import com.example.jpa.board.entity.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;

public interface BoardTypeRepository extends JpaRepository<BoardType, Long> {
    BoardType findByBoardName(@NotBlank(message = "게시판 제목은 필수 항목입니다.") String name);

}
