package com.example.backend.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {
    @Id
    private String email;

    private String password;
    private String nickname;
    private String info;

    @Column(insertable = false, updatable = false)
    private LocalDateTime insertedAt;
}
