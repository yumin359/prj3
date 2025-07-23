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
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            // CustomOAuth2UserService와 OAuth2AuthenticationSuccessHandler를
            // 메서드의 파라미터로 주입받습니다. 이렇게 하면 순환 참조 문제를 피할 수 있습니다.
            // Spring이 이 메서드를 호출할 때, 이미 생성된 이 빈들을 찾아 주입해 줍니다.
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler
    ) throws Exception {
        http
                .csrf(c -> c.disable()) // CSRF 비활성화 (SPA에서 JWT 사용 시 일반적으로 비활성화)
                .authorizeHttpRequests(auth -> auth
                        // 이 경로들은 인증 없이도 접근 가능하도록 허용
                        .requestMatchers("/api/member/login", "/api/member/register", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 로그인 활성화
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorization") // OAuth2 로그인 시작 URL (프론트엔드에서 호출)
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*") // OAuth2 공급자로부터 콜백받는 URL (구글에서 지정한 주소)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 사용자 정보 처리 서비스 등록
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler) // OAuth2 로그인 성공 후 처리할 핸들러 등록
                )
                .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults())); // JWT 리소스 서버 활성화 (JWT 토큰 검증)

        return http.build();
    }

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