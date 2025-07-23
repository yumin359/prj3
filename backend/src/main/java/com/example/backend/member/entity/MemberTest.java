package com.example.backend.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_test")
public class MemberTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password; // 일반 로그인 사용자용, OAuth2 사용자는 null일 수 있음
    private String nickName;
    private String provider; // 'google' 또는 'local'
    private String scope; // 'user', 'admin' 등 권한 (콤마로 구분될 수 있음)
    // 기타 필요한 필드들
}