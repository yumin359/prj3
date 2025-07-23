package com.example.backend.config;

// 필요한 모든 import 문

import com.example.backend.member.service.CustomOAuth2UserService;
import com.example.backend.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration // 이 클래스가 스프링 설정 클래스임을 명시
@EnableMethodSecurity // @PreAuthorize, @PostAuthorize 등을 사용할 수 있게 함
@EnableWebSecurity // 웹 보안 활성화
//@RequiredArgsConstructor
public class AppConfiguration {

    // application.properties에서 주입받을 값들
    @Value("classpath:secret/public.pem")
    private RSAPublicKey publicKey;
    @Value("classpath:secret/private.pem")
    private RSAPrivateKey privateKey;

    @Value("${aws.access.key}")
    private String accessKey;
    @Value("${aws.secret.key}")
    private String secretKey;

    // S3Client 빈 정의 (파일 업로드 등 AWS S3 사용 시 필요)
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        S3Client s3Client = S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // 서울 리전
                .credentialsProvider(provider)
                .build();
        return s3Client;
    }

    // Spring Security 필터 체인 설정
//    @Bean
//    public SecurityFilterChain securityFilterChain() {
//    }

    // JWT 토큰 디코더 (토큰 검증 시 사용)
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    // JWT 토큰 인코더 (토큰 발급 시 사용)
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 정의
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}