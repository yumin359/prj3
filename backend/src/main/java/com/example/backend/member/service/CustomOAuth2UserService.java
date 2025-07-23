package com.example.backend.member.service;

import com.example.backend.member.entity.MemberTest;
import com.example.backend.member.repository.MemberTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service // Spring Bean으로 등록
@RequiredArgsConstructor // 생성자 주입을 위해 Lombok 사용
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberTestRepository memberTestRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2UserService를 사용하여 사용자 정보를 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글에서 제공하는 사용자 정보 맵
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name"); // 또는 'given_name', 'family_name' 등
        String provider = userRequest.getClientRegistration().getRegistrationId(); // 'google'

        // 이메일을 기반으로 기존 회원 찾기
        Optional<MemberTest> optionalMember = memberTestRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            // 새 회원일 경우 Member 엔티티를 생성하고 저장
            MemberTest newMember = new MemberTest();
            newMember.setEmail(email);
            newMember.setNickName(name); // 이름으로 닉네임 초기화
            newMember.setProvider(provider); // 'google'
            newMember.setScope("user"); // 기본 권한 'user'
            // password는 OAuth2 로그인이라 null 또는 랜덤 문자열로 설정 가능
            memberTestRepository.save(newMember);
            System.out.println("새로운 OAuth2 회원 저장: " + email);
        } else {
            // 기존 회원일 경우 정보 업데이트 (예: 닉네임, 최종 로그인 시간 등)
            MemberTest existingMember = optionalMember.get();
            // 필요하다면 기존 회원 정보 업데이트 로직 추가
            // existingMember.setNickName(name);
            // memberRepository.save(existingMember);
            System.out.println("기존 OAuth2 회원 로그인: " + email);
        }

        // OAuth2User 반환 (필요시 커스텀 OAuth2User 구현 가능)
        return oAuth2User;
    }
}