package com.example.backend.member.dto;

import java.time.LocalDateTime;

// 회원 목록 보기(Read-list)에서 사용하기 위한 인터페이스 -> projection 만들 때 사용
public interface MemberListInfo {
    String getEmail();

    String getNickName();

    LocalDateTime getInsertedAt();
}
