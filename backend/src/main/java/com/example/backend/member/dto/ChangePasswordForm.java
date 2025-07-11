package com.example.backend.member.dto;

import lombok.Data;

// 암호 변경 요청시 데이터 담아올 DTO
@Data
public class ChangePasswordForm {
    private String email;
    private String oldPassword;
    private String newPassword;
}
