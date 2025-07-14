package com.example.backend.comment.repository;

import com.example.backend.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // find, save ... 등 기본 메소드 이미 있음
}