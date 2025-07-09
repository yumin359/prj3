package com.example.backend.board.service;

import com.example.backend.board.dto.BoardListInfo;
import com.example.backend.board.entity.Board;
import com.example.backend.board.dto.BoardDto;
import com.example.backend.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시물 작성 Create
    public void add(BoardDto dto) {
        // entity에 dto의 값들 옮겨 담고
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setAuthor(dto.getAuthor());

        // repository에 save 실행
        boardRepository.save(board);
    }

    // 제목, 본문, 작성자 중에 하나라도 안 써지면 false 를 리턴 : 값들이 유효한지 확인하는 메소드
    public boolean validate(BoardDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            return false;
        }
        if (dto.getContent() == null || dto.getContent().trim().isBlank()) {
            return false;
        }
        if (dto.getAuthor() == null || dto.getAuthor().trim().isBlank()) {
            return false;
        }
        return true;
    }

    // 게시물 목록 보기 Read(list)
    public List<BoardListInfo> list() {
        return boardRepository.findAllByOrderByIdDesc();
    }

    // 게시물 하나 보기 Read(one)
    public BoardDto getBoardById(Integer id) {
        Board board = boardRepository.findById(id).get();
        BoardDto boardDto = new BoardDto();
        boardDto.setId(board.getId());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setAuthor(board.getAuthor());
        boardDto.setInsertedAt(board.getInsertedAt());
        return boardDto;
    }

    // 게시물 삭제 Delete
    public void deleteById(Integer id) {
        boardRepository.deleteById(id);
    }

    // 게시물 수정(갱신) Update
    public void update(BoardDto boardDto) {
        // 조회
        Board db = boardRepository.findById(boardDto.getId()).get();
        // 변경
        db.setTitle(boardDto.getTitle());
        db.setContent(boardDto.getContent());
        db.setAuthor(boardDto.getAuthor());
        // 저장
        boardRepository.save(db);
    }
}
