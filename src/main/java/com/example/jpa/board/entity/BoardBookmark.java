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
public class BoardBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    @JoinColumn
    private User user;

    // 북마크 게시글 정보
    @Column
    private long boardId;
    @Column
    private long boardTypeId;
    @Column
    private String boardTitle;
    @Column
    private String boardUrl;

    @Column
    private LocalDateTime regDate;


}
