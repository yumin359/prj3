package com.example.backend.member.service;

import com.example.backend.board.entity.Board;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.board.service.BoardService;
import com.example.backend.comment.repository.CommentRepository;
import com.example.backend.like.repository.BoardLikeRepository;
import com.example.backend.member.dto.*;
import com.example.backend.member.entity.Auth;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.AuthRepository;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtEncoder jwtEncoder;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardService boardService;

    // 회원 가입 Create
    public void add(MemberForm memberForm) {

        if (this.validate(memberForm)) {
            Member member = new Member();
            member.setEmail(memberForm.getEmail());
//            member.setPassword(memberForm.getPassword());
            member.setPassword(passwordEncoder.encode(memberForm.getPassword()));
            // 이제 이러면 테이블에 암호화 되어 저장됨. 복호화 불가능해서 암호 잊어버리면 못 찾음
            member.setNickName(memberForm.getNickName());
            member.setInfo(memberForm.getInfo());
            memberRepository.save(member);
        }
    }

    // 백엔드에서 trim()은 앞 뒤 공백만 확인하고, 중간 공백을 허용함
    // 그래서 그것도 없앨거면 contains나 matches가 필요
    // 근데 이 프로젝트에선 프론트에서도 trim()써서 입력 막아줘서
    // trim()만 해줘도 괜찮음이 아니라
    // trim()은 걍 앞 뒤 공백만 막아주는 거임 둘 다.
    private boolean validate(MemberForm memberForm) {
        // 이미 있는 email 인지
        Optional<Member> db1 = memberRepository.findById(memberForm.getEmail());
        if (db1.isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        // 이미 있는 nickName 인지
        Optional<Member> db2 = memberRepository.findByNickName(memberForm.getNickName());
        if (db2.isPresent()) {
            throw new RuntimeException("이미 사용 중인 별명입니다.");
        }

        // email 있는지
        if (memberForm.getEmail().trim().isBlank()) {
            throw new RuntimeException("이메일을 입력해야 합니다.");
        }
        // 형식에 맞는지
        String email = memberForm.getEmail();
        if (!Pattern.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", email)) {
            throw new RuntimeException("이메일 형식에 맞지 않습니다.");
        }
        // password 있는지
        if (memberForm.getPassword().trim().isBlank()) {
            // trim(없으면 공백 허가?) 입력 안하심 강사님
            // 화면 에서 스페이스바 안 눌리게 함
            throw new RuntimeException("패스워드를 입력해야 합니다.");
        }
        // nickName 있는지
        if (memberForm.getNickName().trim().isBlank()) { // trim 입력 안하심 강사님
            throw new RuntimeException("별명을 입력해야 합니다.");
        }

        return true;
    }

    // 회원 목록 보기 Read(list)
    public List<MemberListInfo> list() {
        return memberRepository.findAllBy();
    }

    // 회원 정보 보기 Read(one)
    public MemberDto get(String email) {
        Member db = memberRepository.findById(email).get();
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(db.getEmail());
        memberDto.setNickName(db.getNickName());
        memberDto.setInfo(db.getInfo());
        memberDto.setInsertedAt(db.getInsertedAt());
        return memberDto;
    }

    public void delete(MemberForm memberForm, Authentication authentication) {
        Member db = memberRepository.findById(memberForm.getEmail()).get();
        // 암호를 암호화해서 저장했으므로
        // 암호화된 암호랑 평문 암호랑 같은지 matches로 확인해야함
        // 앞에가 입력값, 뒤에가 테이블에 저장된 값
//        if (db.getPassword().equals(memberForm.getPassword())) {
        if (passwordEncoder.matches(memberForm.getPassword(), db.getPassword())) {
            // 실제로 지우는 일은 거의 없음.
            // 그래서 값들을 보이는 것만 지워지게 하거나
            // 아니면 다른 컬럼에 옮겨두고 사용자들이 보는 거에는 지우는 것임
            // 하지만 우리는 그냥 연습이기때문에 실제로 지움

            // 회원이 쓴 댓글 지우기
            commentRepository.deleteByAuthor(db);

            // 회원이 쓴 게시물에 달린 댓글 지우기 가 먼저 일어나야함
            ///  회원이 쓴 게시물 얻고
            List<Board> byAuthor = boardRepository.findByAuthor(db);
            ///  그 게시물로 댓글 지우기
            for (Board board : byAuthor) {
                commentRepository.deleteByBoard(board);
            }

            // 회원이 쓴 게시물 지우기
//            boardRepository.deleteByAuthor(db); 이렇게 말구
            /// 1. 회원이 쓴 게시물 번호 목록을 얻고
            List<Integer> boardIdList = boardRepository.listBoardIdByAuthor(db);
            /// 2. 번호 목록을 탐색해서 boardService의 deleteById의 메소드 호출
            for (Integer boardId : boardIdList) {
                boardService.deleteById(boardId, authentication);
            }

            // 좋아요 지우기
            boardLikeRepository.deleteByMember(db);

            // 회원 정보 지우기
            memberRepository.delete(db);
        } else {
            throw new RuntimeException("암호가 일치하지 않습니다.");
        }
    }

    public void update(MemberForm memberForm) {
        // 조회
        Member db = memberRepository.findById(memberForm.getEmail()).get();

        // 암호 확인
        // 얘도 matches 사용해서 입력값과, 테이블에 저장된 암호값과 비교
//        if (!db.getPassword().equals(memberForm.getPassword())) {
        if (!passwordEncoder.matches(memberForm.getPassword(), db.getPassword())) {
            throw new RuntimeException("암호가 일치하지 않습니다.");
        }
        // 변경
        db.setNickName(memberForm.getNickName());
        db.setInfo(memberForm.getInfo());
        // 저장
        memberRepository.save(db);
    }

    // 암호 변경
    public void changePassword(ChangePasswordForm data) {
        Member db = memberRepository.findById(data.getEmail()).get();

        // 얘도 암호화된 암호랑 비교해야 하니까 matches 사용
        // 마찬가지로 앞에가 입력값, 뒤에가 테이블에 있는 값
//        if (db.getPassword().equals(data.getOldPassword())) {
        if (passwordEncoder.matches(data.getOldPassword(), db.getPassword())) {
//            db.setPassword(data.getNewPassword());
            db.setPassword(passwordEncoder.encode(data.getNewPassword()));
            memberRepository.save(db);
        } else {
            throw new RuntimeException("이전 패스워드가 일치하지 않습니다.");
        }
    }

    // 로그인 부분
    public String getToken(MemberLoginForm loginForm) {
        // 해당 이메일의 데이터 있는지
        Optional<Member> db = memberRepository.findById(loginForm.getEmail());
        if (db.isPresent()) {
            // 있으면 패스워드 맞는지
            // 암호를 암호화 해서 저장했으므로 matches를 통해 비교해야함
            // 앞에가 입력받는 값, 뒤에가 저장된 암호화된 값
//            if (db.get().getPassword().equals(loginForm.getPassword())) {
//            if (bCryptPasswordEncoder.matches(loginForm.getPassword(), db.get().getPassword())) {
            // 넘 길어서 shift + f6으로 이름 바꿈
            if (passwordEncoder.matches(loginForm.getPassword(), db.get().getPassword())) {
                // admin 권한 작업중
                List<Auth> authList = authRepository.findByMember(db.get());
                // 고전적 방법
//                String authListString = "";
//                for (Auth auth : authList) {
//                    authListString = authListString + " " + auth.getId().getAuthName();
//                }
//                authListString = authListString.trim();

                // stream 사용
                String authListString = authList.stream()
                        .map(auth -> auth.getId().getAuthName())
                        .collect(Collectors.joining(" "));

                // 둘 다 맞으면 token 만들어서 리턴
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .subject(loginForm.getEmail())
                        .issuer("self")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 365))
                        .claim("scp", authListString)
                        .build();

                return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            }
        }
        throw new RuntimeException("이메일 또는 패스워드가 일치하지 않습니다.");
    }
}
