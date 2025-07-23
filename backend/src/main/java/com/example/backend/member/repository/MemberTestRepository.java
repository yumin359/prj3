package com.example.backend.member.repository;

import com.example.backend.member.entity.MemberTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Spring 빈으로 등록하기 위해 @Repository 어노테이션 사용
public interface MemberTestRepository extends JpaRepository<MemberTest, Long> {
    Optional<MemberTest> findByEmail(String email); // 이메일로 회원 조회
}