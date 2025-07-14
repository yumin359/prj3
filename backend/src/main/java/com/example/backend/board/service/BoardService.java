package com.example.backend.board.service;

import com.example.backend.board.dto.BoardListDto;
import com.example.backend.board.entity.Board;
import com.example.backend.board.dto.BoardDto;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 게시물 작성 Create
    public void add(BoardDto dto, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("권한이 없습니다");
        }

        // entity에 dto의 값들 옮겨 담고
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        // 로그인 한 사용자의 email이 게시물 작성자에 들어감
//        board.setAuthor(authentication.getName());
        // author를 member 테이블과 외래키 해서 이것도 바꿔줌
        Member author = memberRepository.findById(authentication.getName()).get();
        board.setAuthor(author);

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
        // 작성자 확인은 필요 없어서 지움
        return true;
    }

    // 게시물 목록 보기 Read(list) + 검색
//    public List<BoardListInfo> list() {
    public Map<String, Object> list(String keyword, Integer pageNumber) {
//        return boardRepository.findAllByOrderByIdDesc();
        Page<BoardListDto> boardListDtoPage
                = boardRepository.findAllBy(keyword, PageRequest.of(pageNumber - 1, 10));

        int totalPages = boardListDtoPage.getTotalPages(); // 마지막 페이지
        int rightPageNumber = ((pageNumber - 1) / 10 + 1) * 10; // 오른쪽 페이지
        int leftPageNumber = rightPageNumber - 9; // 왼쪽 페이지
        rightPageNumber = Math.min(rightPageNumber, totalPages); // 오른쪽 페이지는 마지막 페이지보다 클 수 없음
        leftPageNumber = Math.max(leftPageNumber, 1); // 왼쪽 페이지는 1페이지보다 작을 수 없음

        var pageInfo = Map.of("totalPages", totalPages,
                "rightPageNumber", rightPageNumber,
                "leftPageNumber", leftPageNumber,
                "currentPageNumber", pageNumber);

        return Map.of("pageInfo", pageInfo,
                "boardList", boardListDtoPage.getContent());
    }

    // 게시물 하나 보기 Read(one)
    public BoardDto getBoardById(Integer id) {
        BoardDto board = boardRepository.findBoardById(id);
//        BoardDto boardDto = new BoardDto();
//        boardDto.setId(board.getId());
//        boardDto.setTitle(board.getTitle());
//        boardDto.setContent(board.getContent());
//        boardDto.setAuthor(board.getAuthor());
//        boardDto.setInsertedAt(board.getInsertedAt());
        return board;
    }

    // 게시물 삭제 Delete
    public void deleteById(Integer id, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("권한이 없습니다.");
        }

        Board db = boardRepository.findById(id).get();

        if (db.getAuthor().getEmail().equals(authentication.getName())) {
            boardRepository.deleteById(id);
        } else {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    // 게시물 수정(갱신) Update
    public void update(BoardDto boardDto, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 조회
        Board db = boardRepository.findById(boardDto.getId()).get();

        if (db.getAuthor().getEmail().equals(authentication.getName())) {
            // 변경
            db.setTitle(boardDto.getTitle());
            db.setContent(boardDto.getContent());
            // 작성자는 정해져있으니까(수정할 필요 없으니까) 지움
//            db.setAuthor(boardDto.getAuthor());

            // 저장
            boardRepository.save(db);
        } else {
            throw new RuntimeException("권한이 없습니다.");
        }
    }
}
