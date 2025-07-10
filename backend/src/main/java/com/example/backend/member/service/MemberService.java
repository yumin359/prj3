package com.example.backend.member.service;

import com.example.backend.member.dto.MemberForm;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입 Create
    public void add(MemberForm memberForm) {

        if (this.validate(memberForm)) {
            Member member = new Member();
            member.setEmail(memberForm.getEmail());
            member.setPassword(memberForm.getPassword());
            member.setNickname(memberForm.getNickName());
            member.setInfo(memberForm.getInfo());
            memberRepository.save(member);
        }
    }

    private boolean validate(MemberForm memberForm) {
        // 이미 있는 email 인지
        // 이미 있는 nickName 인지

        // email 있는지
        if (memberForm.getEmail().trim().isBlank()) {
            throw new RuntimeException("이메일을 입력해야 합니다.");
        }
        // 형식에 맞는지
        String email = memberForm.getEmail();
        if (!Pattern.matches("[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", email)) {
            throw new RuntimeException("이메일 형식에 맞지 않습니다.");
        }
        // password 있는지
        if (memberForm.getPassword().trim().isBlank()) { // trim(없으면 공백 허가?) 입력 안하심 강사님
            throw new RuntimeException("패스워드를 입력해야 합니다.");
        }
        // nickName 있는지
        if (memberForm.getNickName().trim().isBlank()) { // trim 입력 안하심 강사님
            throw new RuntimeException("별명을 입력해야 합니다.");
        }

        return true;
    }
}
