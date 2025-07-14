package com.example.backend.board.controller;

import com.example.backend.board.dto.BoardDto;
import com.example.backend.board.dto.BoardListInfo;
import com.example.backend.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@Controller
//@ResponseBody
@RestController // Controller + ResponseBody
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // <?> : 리턴 타입 안 정해져 있어서 이렇게 씀
    // 이메일은 특수 기호 등 뭐가 많은 문자열 이라서 위처럼 보내는 걸 추천
    // board edit 는 숫자만 보내느 거라서 경로로 보냈던 것!!
    @PutMapping("{id}")
    public ResponseEntity<?> updateBoard(@PathVariable Integer id,
                                         @RequestBody BoardDto boardDto) {
        // 값들이 유효한지 확인하는 메소드를 통해
        boolean result = boardService.validate(boardDto);
        if (result) {
            // 제대로 되었을 때 service에게 일 시키고
            boardService.update(boardDto);
            // react에 보낼 응답
            return ResponseEntity.ok().body(Map.of(
                    "message", Map.of(
                            "type", "success",
                            "text", id + "번 게시물이 수정되었습니다.")));
        } else {
            // 이상할 때 react에 보낼 응답
            return ResponseEntity.ok().body(Map.of(
                    "message", Map.of(
                            "type", "error",
                            "text", "입력한 내용이 유효하지 않습니다.")));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Integer id) {
        // 게시물 삭제 응답 보내기
        boardService.deleteById(id);
        return ResponseEntity.ok().body(Map.of(
                "message", Map.of(
                        "type", "success",
                        "text", id + "번 게시물이 삭제되었습니다."
                )
        ));
    }

    @GetMapping("{id}")
    public BoardDto getBoardById(@PathVariable Integer id) {
        // 게시물 하나 보기
        return boardService.getBoardById(id);
    }

    @GetMapping("list")
    public List<BoardListInfo> getAllBoards() {
        // 게시물 목록 보기
        return boardService.list();
    }

    @PostMapping("add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> add(@RequestBody BoardDto dto,
                                 Authentication authentication) {
        // 값들이 유효한지 확인하는 메소드를 통해
        boolean result = boardService.validate(dto);

        if (result) {
            // 제대로 되었을 때 service에게 일 시키고
            boardService.add(dto, authentication);
            // react에 보낼 응답
            return ResponseEntity.ok().body(
                    Map.of("message",
                            Map.of("type", "success",
                                    "text", "새 글이 저장되었습니다.")));
        } else {
            // 이상할 때 react에 보낼 응답
            return ResponseEntity.badRequest().body(Map.of(
                    "message", Map.of(
                            "type", "error",
                            "text", "입력한 내용이 유효하지 않습니다.")));
        }
    }
}
