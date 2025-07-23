package com.example.backend.member.repository;

import com.example.backend.member.dto.MemberListInfo;
import com.example.backend.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    // 중복된 닉네임을 방지하기 위해 닉네임들을 조회하는 메소드
    // spring data jpa 의 쿼리 메소드이고, 진짜 entity 전체를 조회함
    Optional<Member> findByNickName(String nickName);

    Optional<Member> findByEmail(String email);

    // OAuth2 사용자를 provider와 providerId로 찾는 메서드 추가
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    // 회원 목록 보기 메소드
    // 인터페이스 기반 Projection 이고, 일부 필드만 조회
    List<MemberListInfo> findAllBy();
}