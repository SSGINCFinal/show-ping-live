package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.jwt.JwtUtil;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.service.AuthService;
import com.ssginc.showpinglive.service.MemberService;
import com.ssginc.showpinglive.service.RefreshTokenService;
import com.ssginc.showpinglive.util.EncryptionUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
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
     * 관리자 TOTP 설정 정보 제공 (Secret Key 반환)
     */
    @GetMapping("/totp-setup/{adminId}")
    public ResponseEntity<Map<String, String>> getTotpSetup(@PathVariable String adminId) {
        try {
            Member admin = memberRepository.findByMemberId(adminId).orElse(null);
            if (admin == null) {
                System.out.println("❌ [ERROR] 관리자 계정이 존재하지 않음: " + adminId);
                return ResponseEntity.status(400).body(Map.of("status", "ERROR", "message", "Admin not found"));
            }

            if (admin.getOtpSecretKey() == null) {
                System.out.println("❌ [ERROR] 관리자 OTP 키가 설정되지 않음: " + adminId);
                return ResponseEntity.status(400).body(Map.of("status", "ERROR", "message", "TOTP not set"));
            }

            // ✅ OTP Secret Key 복호화
            String decryptedSecretKey = EncryptionUtil.decrypt(admin.getOtpSecretKey());
            System.out.println("🔑 복호화된 OTP 키: " + decryptedSecretKey);

            String issuer = "ShowPing";
            String qrCodeUrl = "otpauth://totp/" + issuer + ":" + admin.getMemberId() +
                    "?secret=" + decryptedSecretKey + "&issuer=" + issuer;
            System.out.println("✅ QR Code URL 생성 완료: " + qrCodeUrl);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "qrCodeUrl", qrCodeUrl
            ));
        } catch (Exception e) {
            System.out.println("❌ [SERVER ERROR] QR 코드 생성 중 오류 발생");
            e.printStackTrace(); // ✅ 콘솔에 전체 에러 메시지 출력
            return ResponseEntity.status(500).body(Map.of("status", "ERROR", "message", "서버 오류 발생: " + e.getMessage()));
        }
    }


    /**
     * (2) 2FA TOTP 입력 후 인증 (MemberService의 verifyTOTP 사용)
     */
    @PostMapping("/verify-totp")
    public ResponseEntity<Map<String, String>> verifyTotp(@RequestBody Map<String, String> request) {
        String adminId = request.get("adminId");
        int totpCode = Integer.parseInt(request.get("totpCode"));

        ResponseEntity<Map<String, String>> response = authService.verifyTOTP(adminId, totpCode);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("[2차 인증] 성공! 기존 Access Token 반환");

            String accessToken = jwtUtil.generateAccessToken(adminId, "ROLE_ADMIN");
            String refreshToken = jwtUtil.generateRefreshToken(adminId);
            refreshTokenService.saveRefreshToken(adminId, refreshToken);

            System.out.println("🚀 [2차 인증] 유지되는 Access Token: " + accessToken);

            // 기존 응답 + Access Token 추가
            Map<String, String> responseBody = new HashMap<>(response.getBody());
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);

            return ResponseEntity.ok(responseBody);
        } else {
            return response;
        }
    }
}