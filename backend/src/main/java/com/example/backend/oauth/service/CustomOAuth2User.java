package com.example.backend.oauth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

// Spring Security가 내부적으로 사용할 OAuth2User 구현체
public class CustomOAuth2User implements OAuth2User {

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        // OAuth2User의 고유 식별자를 반환합니다.
        // Google의 경우 'sub' 속성이 고유 ID입니다.
        return (String) attributes.get(nameAttributeKey);
    }

    // 추가적으로 필요한 경우 사용자 정의 메서드를 여기에 추가할 수 있습니다.
    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getNickName() {
        return (String) attributes.get("name");
    }
}