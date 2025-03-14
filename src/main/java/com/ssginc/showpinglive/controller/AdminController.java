package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.jwt.JwtUtil;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.service.AuthService;
import com.ssginc.showpinglive.service.MemberService;
import com.ssginc.showpinglive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    /**
     * ✅ 관리자 TOTP 설정 정보 제공 (Secret Key 반환)
     */
    @GetMapping("/totp-setup/{adminId}")
    public ResponseEntity<Map<String, String>> getTotpSetup(@PathVariable String adminId) {
        Member admin = memberRepository.findByMemberId(adminId).orElse(null);
        if (admin == null || admin.getOtpSecretKey() == null) {
            return ResponseEntity.status(400).body(Map.of("status", "ERROR", "message", "Admin not found or TOTP not set"));
        }

        // ✅ Secret Key를 반환하여 사용자가 직접 입력 가능하도록 설정
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "secretKey", admin.getOtpSecretKey() // ✅ QR 코드 대신 Secret Key 반환
        ));
    }

    /**
     * ✅ (1) 로그인 처리 (관리자는 2FA 진행)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String memberId = request.get("adminId");
        String password = request.get("password");

        if (memberId == null || password == null) {
            return ResponseEntity.status(400).body(Map.of("status", "BAD_REQUEST", "message", "Missing required parameters"));
        }

        boolean isPasswordValid = authService.verifyPassword(memberId, password);

        if (!isPasswordValid) {
            return ResponseEntity.status(401).body(Map.of("status", "LOGIN_FAILED"));
        }

        Member member = authService.findMemberById(memberId);

        // ✅ Access Token 및 Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(memberId, member.getMemberRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(memberId);
        refreshTokenService.saveRefreshToken(memberId, refreshToken);

        // ✅ 관리자일 경우 2FA 필요
        if ("ROLE_ADMIN".equals(member.getMemberRole().name())) {
            return ResponseEntity.ok(Map.of(
                    "status", "2FA_REQUIRED",
                    "accessToken", accessToken,  // ✅ 2차 인증 시 사용
                    "refreshToken", refreshToken // ✅ 2차 인증 시 사용
            ));
        }

        // ✅ 일반 사용자는 2차 인증 없이 로그인 성공 처리
        return ResponseEntity.ok(Map.of(
                "status", "LOGIN_SUCCESS",
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }
    /**
     * ✅ (2) 2FA TOTP 입력 후 인증 (MemberService의 verifyTOTP 사용)
     */
    @PostMapping("/verify-totp")
    public ResponseEntity<Map<String, String>> verifyTotp(@RequestBody Map<String, String> request) {
        String adminId = request.get("adminId");
        int totpCode = Integer.parseInt(request.get("totpCode"));

        ResponseEntity<Map<String, String>> response = authService.verifyTOTP(adminId, totpCode);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("✅ [2차 인증] 성공! 기존 Access Token 반환");

            // ✅ 기존 Access Token 유지 (1차 로그인에서 생성한 토큰 사용)
            String accessToken = request.get("accessToken"); // 1차에서 생성된 값 유지
            String refreshToken = request.get("refreshToken"); // 기존 Refresh Token 유지

            System.out.println("🚀 [2차 인증] 유지되는 Access Token: " + accessToken);

            // ✅ 기존 응답 + Access Token 추가
            Map<String, String> responseBody = new HashMap<>(response.getBody());
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);

            return ResponseEntity.ok(responseBody);
        } else {
            return response;
        }
    }
}