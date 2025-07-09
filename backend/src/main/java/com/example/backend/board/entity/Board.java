package com.example.backend.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Entity 즉 테이블 만드는 거라고 생각하면 됨
@Getter
@Setter
@Entity
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String content;
    private String author;

    @Column(updatable = false, insertable = false)
    private LocalDateTime insertedAt;
}
