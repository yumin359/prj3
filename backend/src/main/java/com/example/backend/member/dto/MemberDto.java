package com.example.backend.member.dto;

import lombok.Data;

import java.time.LocalDateTime;

// 회원 정보 보기(Read-one) 시 사용한 DTO
@Data
public class MemberDto {
    private String email;
    private String nickName;
    private String info;
    private LocalDateTime insertedAt;
}
