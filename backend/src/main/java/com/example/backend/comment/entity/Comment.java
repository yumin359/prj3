package com.example.backend.comment.entity;

import com.example.backend.board.entity.Board;
import com.example.backend.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board; // 게시물 번호

    @ManyToOne
    @JoinColumn(name = "author")
    private Member author; // 댓글 작성자

    private String comment; // 댓글 본문

    @Column(insertable = false, updatable = false)
    private LocalDateTime insertedAt; // 댓글 써지는 시간
}
