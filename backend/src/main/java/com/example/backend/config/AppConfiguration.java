package com.example.backend.config;

import com.example.backend.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.example.backend.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // final 필드들을 주입받기 위해 Lombok의 RequiredArgsConstructor 사용
public class AppConfiguration {

    @Value("classpath:secret/public.pem")
    private RSAPublicKey publicKey;
    @Value("classpath:secret/private.pem")
    private RSAPrivateKey privateKey;

    // 실행할 때 값이 들어감
    @Value("${aws.access.key}")
    private String accessKey;
    @Value("${aws.secret.key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        S3Client s3Client = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(provider)
                .build();

        return s3Client;
    }

    private final CustomOAuth2UserService customOAuth2UserService; // CustomOAuth2UserService 빈 주입
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler; // OAuth2AuthenticationSuccessHandler 빈 주입

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (개발 편의상. 실제 서비스에서는 고려)
                .authorizeHttpRequests(authorize -> authorize
                        // /api/** 경로에 대한 접근은 인증된 사용자만 허용
                        .requestMatchers("/api/**").authenticated()
                        // 나머지 모든 요청은 인증 없이 접근 허용
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                                // 사용자 정보를 처리할 커스텀 서비스 지정
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService) // <-- !!! 여기가 CustomOAuth2UserService를 연결하는 핵심 부분입니다 !!!
                                )
                                // OAuth2 인증 성공 시 실행될 핸들러 지정
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                        // (선택 사항) 인증 실패 시 실행될 핸들러 지정 (필요하면 추가)
                        // .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                // (선택 사항) 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃을 처리할 URL
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 URL
                        .invalidateHttpSession(true) // HTTP 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제 (세션 기반 인증 시)
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    // 이거 쓰면 암호를 암호화해서 저장 가능
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}