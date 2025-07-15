package com.example.backend.comment.controller;

import com.example.backend.comment.dto.CommentForm;
import com.example.backend.comment.dto.CommentListDto;
import com.example.backend.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("board/{boardId}")
    public List<CommentListDto> list(@PathVariable Integer boardId) {
        return commentService.listByBoardId(boardId);
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentForm comment,
                                        Authentication authentication) {
        // 로그인 한 사람 정보 가져옴(작성자를 알아야 하니까)
        try {
            commentService.add(comment, authentication);
            return ResponseEntity.ok()
                    .body(Map.of("message",
                            Map.of("type", "success",
                                    "text", "새 댓글이 등록되었습니다.")));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .body(Map.of("message",
                            Map.of("type", "error",
                                    "text", e.getMessage())));

        }

    }
}
