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

    // 게시물 하나 보기 쿼리
    @Query(value = """
                SELECT b.id id,
                       b.title title,
                       b.content content,
                       b.inserted_at inserted_at,
                       m.email author_email,
                       m.nick_name author_nick_name
                FROM board b JOIN member m 
                    ON b.author = m.email
                WHERE b.id = :id
            """, nativeQuery = true)
    BoardDto findBoardById(Integer id);
}