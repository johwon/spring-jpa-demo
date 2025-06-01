package com.example.jpa.board.repository;

import com.example.jpa.board.entity.BoardBookmark;
import com.example.jpa.board.entity.BoardScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, Long> {

}
