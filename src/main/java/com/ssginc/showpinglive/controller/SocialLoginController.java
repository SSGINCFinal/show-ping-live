package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.service.KakaoLoginService;
import com.ssginc.showpinglive.service.NaverLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final KakaoLoginService kakaoLoginService;
    private final NaverLoginService naverLoginService;

    @GetMapping("/oauth/kakao")
    public void redirectKakao(HttpServletResponse response) throws IOException {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=08359b4d82988a73ec2caa499b9744c1"
                + "&redirect_uri=http://localhost:8080/oauth/kakao/callback";
        response.sendRedirect(kakaoAuthUrl);
    }

    @GetMapping("/oauth/naver")
    public void redirectNaver(HttpServletResponse response) throws IOException {
        String naverAuthUrl = "https://nid.naver.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=c9QEcNnQI29B2WMpMXxv"
                + "&redirect_uri=http://localhost:8080/oauth/naver/callback";
        response.sendRedirect(naverAuthUrl);
    }

//    // 카카오에서 Redirect URI로 인가코드 보내줄 때 매핑
//    @GetMapping("/oauth/kakao/callback")
//    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
//        String jwtToken = kakaoLoginService.kakaoLogin(code);
//        return ResponseEntity.ok(jwtToken);
//    }
//
//    // 네이버에서 Redirect URI로 인가코드 보내줄 때 매핑
//    @GetMapping("/oauth/naver/callback")
//    public ResponseEntity<?> naverCallback(@RequestParam String code,
//                                           @RequestParam String state) {
//        String jwtToken = naverLoginService.naverLogin(code, state);
//        return ResponseEntity.ok(jwtToken);
//    }
}
