package com.example.backend.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

// 게시물 하나보기 위한 DTO
@Data
public class BoardDto {
    private Integer id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime insertedAt;

}
