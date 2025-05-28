package com.example.jpa.notice.entity;

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
public class NoticeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn
    private Notice notice;

    @ManyToOne
    @JoinColumn
    private User user;


}
