package CC_BE.CC_BE.config;

import CC_BE.CC_BE.security.CustomUserDetailsService;
import CC_BE.CC_BE.security.JwtAuthenticationFilter;
import CC_BE.CC_BE.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (REST API는 CSRF 보호가 필요없음)
                .csrf(csrf -> csrf.disable())
                // 세션 사용하지 않음 (JWT 등 토큰 기반 인증을 위한 설정)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                // 요청 URL에 따른 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 API 명시 (회원가입, 로그인, 로그아웃)
                        .requestMatchers("/api/users/signup", "/api/users/login", "/api/users/logout").permitAll()
                        // 공용 데이터 조회는 누구나 접근 가능
                        .requestMatchers(HttpMethod.GET, "/api/brands/**", "/api/categories/**", "/api/models/**").permitAll()
                        .requestMatchers("/api/models/category/*/public").permitAll()
                        .requestMatchers("/api/manuals/model/*/download").permitAll()
                        // 챗봇 API는 누구나 접근 가능
                        .requestMatchers("/api/chat/**").permitAll()
                        // 인증된 사용자만 접근 가능
                        .requestMatchers("/api/models/personal/**").authenticated()
                        // 관리자만 접근 가능 (생성/수정/삭제)
                        .requestMatchers("/api/models/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/brands/**", "/api/categories/**", "/api/models/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/brands/**", "/api/categories/**", "/api/models/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/brands/**", "/api/categories/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    // AuthenticationManager 빈을 등록하여 서비스 등에서 주입받을 수 있도록 함
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
        return new BCryptPasswordEncoder();
    }
}
