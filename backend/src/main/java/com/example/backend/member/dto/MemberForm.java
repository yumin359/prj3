package com.example.backend.member.dto;

import lombok.Data;

// 회원 가입시 값 받아올 DTO
// 아마도 회사에선 다 나누는 게 좋지만, 공부용이라서
// 수정, 삭제 시에도 이 DTO 사용
@Data
public class MemberForm {
    private String email;
    private String password;
    private String nickName;
    private String info;
}
