package com.example.backend.member.controller;

import com.example.backend.member.dto.*;
import com.example.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

//    @PreAuthorize("isAuthenticated() or hasAuthority('SCOPE_admin')")
    // 로그인 한 사용자만 접근 가능 또는
    // 특정 권한이 있는 사용자만 허용 -> scope이 admin인 사람만 접근 가능

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody MemberLoginForm loginForm) {
//        System.out.println("loginForm = " + loginForm);
        try {
            String token = memberService.getToken(loginForm);
            return ResponseEntity.ok().body(
                    Map.of("token", token,
                            "message",
                            Map.of("type", "success",
                                    "text", "로그인 되었습니다.")));
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            return ResponseEntity.status(401).body(
                    Map.of("message",
                            Map.of("type", "error",
                                    "text", message)));
        }

    }

    @PutMapping("changePassword")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordForm data,
                                            Authentication authentication) {
//        System.out.println("data = " + data);
        // 암호 변경 본인 것만 가능
        if (!authentication.getName().equals(data.getEmail())) {
            return ResponseEntity.status(403).build();
        }
        try {
            memberService.changePassword(data);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            return ResponseEntity.status(403).body(
                    Map.of("message",
                            Map.of("type", "error",
                                    "text", message)));
        }
        return ResponseEntity.ok().body(
                Map.of("message",
                        Map.of("type", "success",
                                "text", "암호가 변경되었습니다.")));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@RequestBody MemberForm memberForm,
                                    Authentication authentication) {
        // 얘도 저장폼따로 만들어서 써도 됨
        // 회원 정보 수정 본인 것만 가능
        if (!authentication.getName().equals(memberForm.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        try {
            memberService.update(memberForm);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            return ResponseEntity.status(403).body(
                    Map.of("message",
                            Map.of("type", "error",
                                    "text", message)));
        }
        return ResponseEntity.ok().body(
                Map.of("message",
                        Map.of("type", "success",
                                "text", "회원 정보가 수정되었습니다.")));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMember(@RequestBody MemberForm memberForm,
                                          Authentication authentication) {
        // memberdeleteform 따로 만들어서 써도 됨
        // 회사라면 만들었을 듯

        // 회원 정보 삭제 본인 것만 가능
        if (!authentication.getName().equals(memberForm.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        try {
            memberService.delete(memberForm);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            return ResponseEntity.status(403).body(
                    // 403 : 권한 없음, 401 : 로그인 안 됨(인증 안 됨)
                    Map.of("message",
                            Map.of("type", "error",
                                    "text", message)));
        }
        return ResponseEntity.ok().body(
                Map.of("message",
                        Map.of("type", "success",
                                "text", "회원 정보가 삭제되었습니다.")));
    }

    // 이메일은 특수 기호 등 뭐가 많은 문자열 이라서 위처럼 보내는 걸 추천
    // board edit 는 숫자만 보내느 거라서 경로로 보냈던 것!!
    @GetMapping(params = "email")
    @PreAuthorize("isAuthenticated() or hasAuthority('SCOPE_admin')")
    // admin은 남의 정보 보는 것만 가능
    // 수정/삭제 불가능
    public ResponseEntity<?> getMember(String email,
                                       Authentication authentication) {
        if (authentication.getName().equals(email) || // 자기 거거나
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_admin"))) {
            // admin이 있으면
            return ResponseEntity.ok().body(memberService.get(email));
        } else {
            return ResponseEntity.status(403).build();
        }
        // 로그인한 사용자는 자기 정보만 볼 수 있음.
        // 로그아웃 사용자는 아무 회원 정보를 못 봄
    }

    // admin만 보이게 수정할 것임(원래 로그인 하면 다 볼 수 있었음)
    @GetMapping("list")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    // 즉 admin 인 trump로 로그인 됐을 때만 회원목록 볼 수 있음
    public List<MemberListInfo> list() {
        return memberService.list();
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@RequestBody MemberForm memberForm) {
        try {
            memberService.add(memberForm);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            Map.of("type", "error",
                                    "text", message)));
        }
        return ResponseEntity.ok().body(
                Map.of("message",
                        Map.of("type", "success",
                                "text", "회원 가입 되었습니다.")));
    }
}
