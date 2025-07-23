package com.example.backend.member.repository;

import com.example.backend.member.entity.MemberTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTestRepository extends JpaRepository<MemberTest, Long> {
    Optional<MemberTest> findByEmail(String email);

    boolean existsByEmail(String email);
}