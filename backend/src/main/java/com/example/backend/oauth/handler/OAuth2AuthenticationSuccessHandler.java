package com.example.backend.oauth.handler;

import com.example.backend.member.entity.Member;
import com.example.backend.member.entity.MemberTest;
import com.example.backend.member.repository.MemberTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Component // Spring Bean으로 등록
@RequiredArgsConstructor // JwtEncoder와 MemberRepository를 생성자 주입
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtEncoder jwtEncoder;
    private final MemberTestRepository memberTestRepository;

    @Value("${app.oauth2.redirectUri}") // application.properties에서 값 주입
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. OAuth2User 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        // String name = (String) oAuth2User.getAttributes().get("name"); // 필요하면 사용

        // 2. 이메일로 회원 정보 조회 (CustomOAuth2UserService에서 이미 저장/업데이트됨)
        Optional<MemberTest> optionalMember = memberTestRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            // 예외 처리: 만약 CustomOAuth2UserService에서 회원 저장이 실패했거나 로직 오류가 있다면
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 로그인 후 회원 정보를 찾을 수 없습니다.");
            return;
        }
        MemberTest member = optionalMember.get();

        // 3. JWT 토큰 생성
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // 토큰 발급자
                .issuedAt(now) // 발급 시간
                .expiresAt(now.plus(1, ChronoUnit.HOURS)) // 만료 시간 (1시간)
                .subject(member.getEmail()) // 토큰의 주체 (여기서는 이메일)
                .claim("scope", member.getScope()) // 사용자 권한 (예: "user", "admin")
                .claim("nickname", member.getNickName()) // 닉네임 (프론트엔드에서 사용)
                .claim("provider", member.getProvider()) // 소셜 로그인 제공자 ('google')
                .build();

        String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // 4. 프론트엔드 리디렉션 URL 구성 (JWT 토큰 포함)
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token) // JWT 토큰을 'token' 파라미터로 추가
                .build().toUriString();

        // 5. 브라우저를 프론트엔드 URL로 리디렉션
        response.sendRedirect(targetUrl);
    }
}