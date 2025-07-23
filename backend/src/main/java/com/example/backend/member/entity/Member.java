package com.example.backend.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 회원 Entity
@Getter
@Setter
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
}
