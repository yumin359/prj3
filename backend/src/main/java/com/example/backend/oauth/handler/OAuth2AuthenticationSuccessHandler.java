package com.example.backend.oauth.handler;

import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j 로거 임포트
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j // Slf4j 로거 사용
@Component // Spring 빈으로 등록하기 위해 @Component 어노테이션 사용
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository; // MemberTestRepository 주입

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("--- OAuth2AuthenticationSuccessHandler 시작 ---");

        // 인증 객체에서 OAuth2User 정보 가져오기
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();

        log.debug("Authentication Principal: {}", oauth2User);
        log.debug("OAuth2User Attributes: {}", oauth2User.getAttributes());

        String email = (String) oauth2User.getAttributes().get("email");

        if (email == null) {
            log.error("OAuth2 인증 성공 후 이메일 정보를 얻을 수 없습니다.");
            response.sendRedirect("/error?message=email_not_found"); // 에러 페이지로 리다이렉션
            return;
        }

        // CustomOAuth2UserService에서 이미 회원 가입/업데이트를 처리했으므로, 여기서는 단순히 확인만 하거나 추가 로직 수행
        // memberTestRepository.findByEmail(email) 호출은 CustomOAuth2UserService에서 이미 했으므로 여기서는 불필요할 수 있습니다.
        // 다만, 추가적인 정보 확인 또는 세션/JWT 발급 등의 로직을 넣을 수 있습니다.
        boolean isMemberExists = memberRepository.findByEmail(email).isPresent();

        if (isMemberExists) {
            log.info("OAuth2 인증 성공, 이메일: {} (DB에서 확인됨)", email);
            // 로그인 성공 후 리다이렉트할 URL
            response.sendRedirect("http://localhost:5173/main"); // <-- 프론트엔드 메인 페이지로 리다이렉션 예시
        } else {
            // 이 로직은 CustomOAuth2UserService가 제대로 작동했다면 사실상 도달할 수 없어야 합니다.
            // CustomOAuth2UserService에서 DB 저장을 처리했어야 하기 때문입니다.
            log.error("DB에서 회원({})을 찾을 수 없습니다. CustomOAuth2UserService에서 가입이 누락되었을 수 있습니다.", email);
            response.sendRedirect("/error?message=user_not_found"); // 에러 페이지로 리다이렉션
        }
    }
}