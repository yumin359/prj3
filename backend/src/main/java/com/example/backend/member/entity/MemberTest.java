package com.example.backend.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_test") // 테이블 이름 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 이메일은 중복되지 않아야 함
    private String email;

    @Column(nullable = true) // OAuth2 로그인은 비밀번호가 없을 수 있으므로 nullable=true
    private String password;

    @Column(nullable = true) // 닉네임은 필수 아닐 수 있음
    private String nickName;

    @Column(nullable = false) // 어떤 OAuth2 제공자로 로그인했는지 (google, kakao 등)
    private String provider;

    @Column(nullable = false) // 사용자 권한 (ROLE_USER, ROLE_ADMIN 등)
    private String scope;

    // 필요한 경우 사용자 정보 업데이트 메서드 추가
    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }
    // 다른 필드 업데이트 메서드도 필요하다면 추가
}