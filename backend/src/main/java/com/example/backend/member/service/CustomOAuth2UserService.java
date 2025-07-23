package com.example.backend.member.service;

import com.example.backend.member.entity.MemberTest; // MemberTest를 사용한다고 하셨으니 이 import가 맞는지도 확인해주세요
import com.example.backend.member.repository.MemberTestRepository; // 이것도 MemberTestRepository가 맞는지 확인
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional // Transactional 어노테이션이 여기에 잘 붙어있는지 다시 확인
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final MemberTestRepository memberTestRepository; // memberTestRepository로 필드명도 변경되었는지 확인해주세요

//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//    }
}