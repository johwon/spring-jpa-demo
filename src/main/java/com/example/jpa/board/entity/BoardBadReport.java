package com.example.jpa.board.entity;

import com.example.jpa.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardBadReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    // 신고자 정보
    @Column
    private long userId;
    @Column
    private String userName;
    @Column
    private String userEmail;

    // 신고 게시글 정보
    @Column
    private long boardId;
    @Column
    private long boardUserId;
    @Column
    private String boardTitle;
    @Column
    private String boardContents;
    @Column
    private LocalDateTime boardRegDate;
    
    // 신고 내용
    @Column
    private String comments;
    @Column
    private LocalDateTime regDate;


}
