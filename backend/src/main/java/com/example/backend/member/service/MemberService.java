package com.example.backend.member.service;

import com.example.backend.member.dto.*;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtEncoder jwtEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void delete(MemberForm memberForm) {
        Member db = memberRepository.findById(memberForm.getEmail()).get();
        if (db.getPassword().equals(memberForm.getPassword())) {
            memberRepository.delete(db);
        } else {
            throw new RuntimeException("암호가 일치하지 않습니다.");
        }
    }

    // 회원 가입 Create
    public void add(MemberForm memberForm) {

        if (this.validate(memberForm)) {
            Member member = new Member();
            member.setEmail(memberForm.getEmail());
//            member.setPassword(memberForm.getPassword());
            member.setPassword(bCryptPasswordEncoder.encode(memberForm.getPassword()));
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

    public void update(MemberForm memberForm) {
        // 조회
        Member db = memberRepository.findById(memberForm.getEmail()).get();

        // 암호 확인
        if (!db.getPassword().equals(memberForm.getPassword())) {
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

        if (db.getPassword().equals(data.getOldPassword())) {
            db.setPassword(data.getNewPassword());
            memberRepository.save(db);
        } else {
            throw new RuntimeException("이전 패스워드가 일치하지 않습니다.");
        }
    }

    public String getToken(MemberLoginForm loginForm) {
        // 해당 이메일의 데이터 있는지
        Optional<Member> db = memberRepository.findById(loginForm.getEmail());
        if (db.isPresent()) {
            // 있으면 패스워드 맞는지
            if (db.get().getPassword().equals(loginForm.getPassword())) {
                // 둘 다 맞으면 token 만들어서 리턴
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .subject(loginForm.getEmail())
                        .issuer("self")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 365))
                        .build();

                return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            }
        }
        throw new RuntimeException("이메일 또는 패스워드가 일치하지 않습니다.");
    }
}
