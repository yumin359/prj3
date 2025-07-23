//package com.example.backend.oauth.service;
//
//import com.example.backend.member.entity.Member;
//import com.example.backend.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j; // Slf4j 로거 임포트
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional; // 트랜잭션 임포트
//
//import java.util.Map;
//import java.util.Optional;
//
//@Slf4j // Lombok의 Slf4j 로거 사용 (System.out.println 대신 권장)
//@Service // Spring 빈으로 등록하기 위해 @Service 어노테이션 사용
//@RequiredArgsConstructor
//@Transactional // DB 작업에 트랜잭션 적용
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final MemberRepository memberRepository; // MemberTestRepository 주입
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        // 로거를 통해 디버그 메시지 출력
//        log.debug("CustomOAuth2UserService - loadUser 메서드 진입");
//
//        // OAuth2 공급자로부터 사용자 정보 로드 (기본 서비스 사용)
//        OAuth2User oauth2User = super.loadUser(userRequest);
//
//        log.debug("OAuth2User attributes: {}", oauth2User.getAttributes());
//
//        // registrationId는 현재 로그인 진행 중인 서비스를 구분 (google, kakao, naver 등)
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
//                .getUserInfoEndpoint().getUserNameAttributeName();
//
//        Map<String, Object> attributes = oauth2User.getAttributes();
//        String email = (String) attributes.get("email"); // Google은 'email' 속성을 제공
//
//        log.debug("OAuth2 authentication successful for email: {}", email);
//
//        // 이메일을 기준으로 회원 조회 또는 저장/업데이트
//        Optional<Member> memberOptional = memberRepository.findByEmail(email);
//        Member member;
//
//        if (memberOptional.isPresent()) {
//            // 이미 존재하는 회원인 경우 정보 업데이트
//            member = memberOptional.get();
//            // 필요한 경우 닉네임, 프로필 이미지 등 정보 업데이트 로직 추가
//            log.debug("Existing user found: {}", member.getEmail());
//            // 예시: member.updateNickName((String) attributes.get("name"));
//        } else {
//            // 새로운 회원인 경우 저장
//            log.debug("New user detected. Saving to DB: {}", email);
//            member = Member.builder()
//                    .email(email)
//                    // Google에서 제공하는 닉네임 사용 (없으면 기본값 또는 예외 처리)
//                    .nickName((String) attributes.get("name"))
//                    .password("oauth_user") // OAuth2 사용자는 비밀번호를 사용하지 않으므로 임의의 값 설정
//                    .provider(registrationId) // Google
//                    .scope("ROLE_USER") // 기본 역할 부여
//                    .build();
//            memberRepository.save(member);
//            log.debug("New user saved: {}", member.getEmail());
//        }
//
//        // Spring Security가 인증 객체를 생성할 때 사용할 OAuth2User 반환
//        return new CustomOAuth2User(oauth2User.getAuthorities(), attributes, userNameAttributeName);
//    }
//}
// 예시: CustomOAuth2UserService.java
package com.example.backend.oauth.service;
//package com.example.backend.oauth2.service; // 패키지명은 실제 경로에 맞춰주세요

import com.example.backend.member.entity.Member; // Member 엔티티 임포트
import com.example.backend.member.repository.MemberRepository; // MemberRepository 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional; // Optional 임포트

@Service
@RequiredArgsConstructor // MemberRepository 주입을 위한 Lombok 어노테이션
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository; // MemberRepository 사용

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email"); // Google에서 제공하는 이메일
        String name = (String) attributes.get("name"); // Google에서 제공하는 이름
        String sub = (String) attributes.get("sub"); // Google의 고유 ID (providerId로 사용)

        // 1. 이메일로 기존 회원 조회 (소셜 로그인 연동 고려)
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        Member member;

        if (memberOptional.isPresent()) {
            // 2. 이 이메일로 이미 가입된 회원이 있다면 (기존 일반 회원 또는 다른 소셜 회원)
            member = memberOptional.get();
            // 만약 기존 일반 회원인데 소셜 로그인을 처음 시도한다면 provider, providerId 업데이트
            if (member.getProvider() == null || !member.getProvider().equals(provider)) {
                member.setProvider(provider);
                member.setProviderId(sub);
                memberRepository.save(member);
                System.out.println("기존 회원 (" + email + ")에 OAuth2 정보 업데이트.");
            }
            System.out.println("기존 OAuth2 회원 로그인: " + email);
        } else {
            // 3. 이메일로 찾을 수 없다면, 새로운 OAuth2 회원으로 가입
            member = Member.builder()
                    .email(email)
                    .nickName(name) // Google 이름 사용 (닉네임 중복 처리 필요할 수 있음)
                    .password(null) // OAuth2 로그인은 비밀번호 없음 (DB 스키마 nullable=true 필요)
                    .provider(provider) // "google"
                    .providerId(sub) // Google의 고유 ID
                    .scope("ROLE_USER") // 기본 권한 부여
                    .insertedAt(LocalDateTime.now())
                    .build();
            memberRepository.save(member);
            System.out.println("새로운 OAuth2 회원 가입: " + email);
        }

        // 중요: Spring Security는 OAuth2User 객체를 유지해야 합니다.
        // 필요한 경우 custom UserDetails 구현체를 반환하거나,
        // oAuth2User를 확장하여 추가 정보를 포함시킬 수 있습니다.
        // 여기서는 간단히 oAuth2User를 반환합니다.
        return oAuth2User;
    }
}