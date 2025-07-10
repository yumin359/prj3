package com.example.backend.member.service;

import com.example.backend.member.dto.MemberForm;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입 Create
    public void add(MemberForm memberForm) {
        Member member = new Member();
        member.setEmail(memberForm.getEmail());
        member.setPassword(memberForm.getPassword());
        member.setNickname(memberForm.getNickName());
        member.setInfo(memberForm.getInfo());
        memberRepository.save(member);
    }
}
