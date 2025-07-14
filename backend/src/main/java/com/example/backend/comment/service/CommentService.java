package com.example.backend.comment.service;

import com.example.backend.board.entity.Board;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.comment.dto.CommentForm;
import com.example.backend.comment.entity.Comment;
import com.example.backend.comment.repository.CommentRepository;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public void add(CommentForm comment, Authentication authentication) {
        // 로그인 한 사용자가 없을 때 = 로그인 안 하고 댓글 못 씀
        if (authentication == null) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // board entity 에서 게시물 번호 가져옴
        Board board = boardRepository.findById(comment.getBoardId()).get();
        // member entity 에서 로그인한 사용자명 가져옴 = id를 가져오는건데 우리는 그래서 email을 가져오게 됨
        Member member = memberRepository.findById(authentication.getName()).get();

        // Comment Entity 에 각 정보를 넣어서 저장함
        Comment db = new Comment();
        db.setBoard(board);
        db.setComment(comment.getComment()); // 얘는 화면에 쓴 거 그대로 가져오는거고
        db.setAuthor(member);

        commentRepository.save(db);
    }
}
