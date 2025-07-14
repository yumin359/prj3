package com.example.backend.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "auth", schema = "prj3")
public class Auth {
    @EmbeddedId
    private AuthId id; // 합성키라 EmbeddedId

    @MapsId("memberEmail") // 이거는 합성키중에 뭐시기..
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_email", nullable = false)
//    private Member memberEmail;
    private Member member; // 위에거로 생성됐는데 이렇게 바꿈

}