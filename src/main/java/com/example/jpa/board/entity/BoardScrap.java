package com.example.jpa.board.entity;

import com.example.jpa.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    @JoinColumn
    private User user;

    // 스크랩 게시글 정보
    @Column
    private long boardId;
    @Column
    private long boardTypeId;
    @Column
    private long boardUserId;
    @Column
    private String boardTitle;
    @Column
    private String boardContents;
    @Column
    private LocalDateTime boardRegDate;
    
    @Column
    private LocalDateTime regDate;


}
