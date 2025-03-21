package com.ssginc.showpinglive.config;

import com.ssginc.showpinglive.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("https://showping.duckdns.org"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 X (JWT 방식)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "webrtc/watch", "/css/**", "/js/**", "/images/**",
                                "/assets/**", "/oauth/**", "/api/register", "/api/auth/login", "/api/auth/logout", "api/auth/user-info").permitAll()
                        .requestMatchers("/admin/**", "/report/report", "webrtc/webrtc", "stream/stream").hasRole("ADMIN") //admin, chat/chat 주소 접근은 ADMIN role만 접근 가능
                        .requestMatchers("/user/**", "watch/history").hasAnyRole("USER", "ADMIN") //user 주소 접근은 ADMIN/USER role중 어떤 것이든 접근 가능
                        .anyRequest().permitAll() //모두 허용하고, admin과 user로 시작하는 사이트와 /chat/chat만 로그인 필요
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // ✅ JWT 필터 적용

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Thymeleaf에서 Security 기능 사용 가능하도록 설정
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    // 로그인 성공 시 처리 (메인페이지로 리다이렉트)
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> response.sendRedirect("/");
    }

    // 로그아웃 성공 시 처리 (로그아웃 후 로그인 페이지로 이동)
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> response.sendRedirect("/login");
    }

}