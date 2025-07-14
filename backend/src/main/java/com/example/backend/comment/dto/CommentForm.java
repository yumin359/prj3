package com.example.backend.comment.dto;

import lombok.Data;

// 댓글 입력(Create) DTO
@Data
public class CommentForm {
    private Integer boardId; // 게시물 번호
    private String comment; // 댓글 본문
}
