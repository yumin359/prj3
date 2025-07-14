package com.example.backend.board.repository;

import com.example.backend.board.dto.BoardDto;
import com.example.backend.board.dto.BoardListDto;
import com.example.backend.board.dto.BoardListInfo;
import com.example.backend.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    // 게시물 목록 보기 projection 으로 받아옴
    List<BoardListInfo> findAllByOrderByIdDesc();

    // naviteQuery로 하면 뭐할게 많아서
    // JPQL로 하신대용
    // 게시물 목록 보기 쿼리
    @Query(value = """
                SELECT new com.example.backend.board.dto.BoardListDto(
                            b.id,
                            b.title,
                            m.nickName,
                            b.insertedAt)
                FROM Board b JOIN Member m
                        ON b.author.email = m.email
                ORDER BY b.id DESC
            """)
    List<BoardListDto> findAllBy();

    // 순서가 같아야 함 BoradDto에서 쓴 거랑
    // 얘도 JPQL로 바꿈
    // 게시물 하나 보기 쿼리
    @Query(value = """
                SELECT new com.example.backend.board.dto.BoardDto
                     ( b.id,
                       b.title,
                       b.content,
                       m.email,
                       m.nickName,
                       b.insertedAt)
                FROM Board b JOIN Member m
                    ON b.author.email = m.email
                WHERE b.id = :id
            """)
    BoardDto findBoardById(Integer id);
}