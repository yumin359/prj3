package com.example.backend.board.repository;

import com.example.backend.board.dto.BoardDto;
import com.example.backend.board.dto.BoardListDto;
import com.example.backend.board.dto.BoardListInfo;
import com.example.backend.board.entity.Board;
import com.example.backend.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    // 게시물 목록 보기 projection 으로 받아옴
    List<BoardListInfo> findAllByOrderByIdDesc();

    // naviteQuery로 하면 뭐할게 많아서
    // JPQL로 하신대용
    // 게시물 목록 보기 쿼리 + 검색까지
    // JPQL에서는 subquery가 안 됨
    // 그래서 LEFT 조인으로 해줬어용
    @Query(value = """
                SELECT new com.example.backend.board.dto.BoardListDto(
                            b.id,
                            b.title,
                            m.nickName,
                            b.insertedAt,
                            COUNT(DISTINCT c),
                            COUNT(DISTINCT l),
                            COUNT(DISTINCT f))
                FROM Board b JOIN Member m
                            ON b.author.email = m.email
                    LEFT JOIN Comment c
                            ON b.id = c.board.id
                    LEFT JOIN BoardLike l
                            ON b.id = l.board.id
                    LEFT JOIN BoardFile f
                            ON b.id = f.board.id
                WHERE b.title LIKE %:keyword%
                   OR b.content LIKE %:keyword%
                   OR m.nickName LIKE %:keyword%
                GROUP BY b.id
                ORDER BY b.id DESC
            """)
    Page<BoardListDto> findAllBy(String keyword, PageRequest pageRequest);
//    List<BoardListDto> findAllBy(String keyword, PageRequest pageRequest);

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

    void deleteByAuthor(Member author);

    List<Board> findByAuthor(Member db);

    @Query("""
            SELECT b.id
            FROM Board b
            WHERE b.author = :author
            """)
    List<Integer> listBoardIdByAuthor(Member author);
}