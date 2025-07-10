package com.example.backend.member.dto;

import lombok.Data;

// 회원 가입시 값 받아올 DTO
@Data
public class MemberForm {
    private String email;
    private String password;
    private String nickName;
    private String info;
}
