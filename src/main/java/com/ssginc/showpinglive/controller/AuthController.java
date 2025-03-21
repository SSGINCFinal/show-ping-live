package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.jwt.JwtUtil;
import com.ssginc.showpinglive.service.AuthService;
import com.ssginc.showpinglive.service.MemberService;
import com.ssginc.showpinglive.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController  // ✅ JSON 응답을 반환하도록 변경
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

    private final MemberService memberService;

    private final JwtUtil jwtUtil;
    /**
     * (1) 로그인 처리 (관리자는 2FA 진행)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String memberId = request.get("memberId");
        String password = request.get("password");

        if (memberId == null || password == null) {
            return ResponseEntity.status(400).body(Map.of("status", "BAD_REQUEST", "message", "Missing required parameters"));
        }

        Member member = memberService.findMember(memberId, password);

        if (member != null) {
            if (member.getMemberRole().name().equals("ROLE_USER")) {
                // Access Token 및 Refresh Token 생성
                String accessToken = jwtUtil.generateAccessToken(memberId, "ROLE_USER");
                String refreshToken = jwtUtil.generateRefreshToken(memberId);
                refreshTokenService.saveRefreshToken(memberId, refreshToken);

                // 일반 사용자는 2차 인증 없이 로그인 성공 처리
                return ResponseEntity.ok(Map.of(
                        "status", "LOGIN_SUCCESS",
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                ));
            }
            else if (member.getMemberRole().name().equals("ROLE_ADMIN")) {
                return ResponseEntity.ok(Map.of(
                        "status", "2FA_REQUIRED"));
            }

        }

        return ResponseEntity.status(401).body(Map.of("status", "LOGIN_FAILED"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        System.out.println("📢 로그아웃 요청 도착!");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔍 현재 SecurityContext 상태: " + authentication);

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {

            String username = authentication.getName(); // username 가져오기
            System.out.println("📢 로그아웃 처리 중: " + username);

            refreshTokenService.deleteRefreshToken(username); // Redis에서 Refresh Token 삭제
            authService.logout(username, response); // Access Token 삭제 (쿠키 삭제)
            SecurityContextHolder.clearContext();

            System.out.println("로그아웃 완료!");
        } else {
            System.out.println("로그아웃 실패: 인증된 사용자 없음");
        }

        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }


    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/user-info")
    public ResponseEntity<Map<String, String>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("message", "인증되지 않은 사용자입니다."));
        }

        String username = authentication.getName();
        return ResponseEntity.ok(Map.of("username", username));
    }

    @GetMapping("/refresh-token-check")
    public ResponseEntity<?> checkRefreshToken(@RequestParam String username) {
        String token = refreshTokenService.checkRefreshToken(username);
        if (token != null) {
            return ResponseEntity.ok(Map.of("refreshToken", token));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "저장된 Refresh Token 없음"));
        }
    }
}

