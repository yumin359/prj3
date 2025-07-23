package com.example.backend.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

// 회원 Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@DynamicInsert
@Entity
@Table(name = "member")
public class Member {
    @Id
    private String email;

    private String password; // 일반 로그인은 사용, 구글 로그인은 null
    private String nickName;
    private String info;

//    private String provider; // "local" 또는 "google"

    @Column(insertable = false, updatable = false)
    private LocalDateTime insertedAt;

    // OAuth2 관련 필드 추가
    @Column(nullable = true) // OAuth2 사용자가 아니면 null
    private String provider;

    @Column(name = "provider_id", nullable = true) // OAuth2 사용자가 아니면 null
    private String providerId; // Google의 'sub' 값 저장

    // 권한 필드 추가 (String 또는 List<String>으로 정의 가능)
    private String scope; // 쉼표 등으로 구분된 문자열 (예: "ROLE_USER,ROLE_ADMIN")
    // 아니면 List<String>으로 정의하고 @ElementCollection 사용 가능

}
