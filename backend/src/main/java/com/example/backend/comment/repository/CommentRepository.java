package com.example.backend.comment.repository;

import com.example.backend.comment.dto.CommentListDto;
import com.example.backend.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // find, save ... 등 기본 메소드 이미 있음

    // 댓글 목록 보기 JPQL
    @Query("""
            SELECT new com.example.backend.comment.dto.CommentListDto (
                c.id,
                c.board.id,
                c.author.email,
                c.author.nickName,
                c.comment,
                c.insertedAt
                )
            FROM Comment c JOIN c.author m
            WHERE c.board.id = :boardId
            ORDER BY c.id DESC
            """)
    List<CommentListDto> listByBoardId(Integer boardId);

    void deleteByBoardId(Integer boardId);
}