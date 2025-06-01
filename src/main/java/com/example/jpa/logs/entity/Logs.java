package com.example.jpa.logs.entity;

import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.UserPointType;
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
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String text;

    @Column
    private LocalDateTime regDate;

}
