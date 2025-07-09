package com.example.backend.board.controller;

import com.example.backend.board.dto.BoardDto;
import com.example.backend.board.dto.BoardListInfo;
import com.example.backend.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id) {
        System.out.println("BoardController.update");
        return null;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        boardService.delete(id);
        return ResponseEntity.ok().body(
                Map.of("message",
                        Map.of("type", "success",
                                "text", "게시물이 삭제되었습니다."))
        );
    }

    @GetMapping("{id}")
    public BoardDto getBoardById(@PathVariable Integer id) {
        return boardService.getBoardById(id);
    }

    @GetMapping("list")
    public List<BoardListInfo> getAllBoards() {

        return boardService.list();
    }

    @PostMapping("add")
    public ResponseEntity<Object> add(@RequestBody BoardDto dto) {
        // 값들이 유효한지 확인
        boolean result = boardService.validate(dto);

        if (result) {
            // service에게 넘겨서 일 시키기
            boardService.add(dto);

            return ResponseEntity.ok().body(
                    Map.of("message",
                            Map.of("type", "success",
                                    "text", "새 글이 저장되었습니다.")));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", Map.of(
                            "type", "error",
                            "text", "입력한 내용이 유효하지 않습니다.")));
        }
    }
}
