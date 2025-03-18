package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.object.KakaoUserInfo;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.jwt.JwtProvider;
import com.ssginc.showpinglive.service.KakaoLoginService;
import com.ssginc.showpinglive.service.implement.KakaoLoginServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoLoginService kakaoLoginService;
    private final JwtProvider jwtProvider;

    // 카카오 로그인 완료 후 Redirect URI에 인가 코드가 전달됨
    @GetMapping("/oauth/kakao/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        // 1. **우리 서버 JWT** 발급
        String serverJwt = kakaoLoginService.kakaoLogin(code);

        // 2. 클라이언트가 sessionStorage에 저장하도록 HTML 반환
        response.setContentType("text/html;charset=UTF-8");
        String htmlResponse = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta charset='UTF-8'>\n"
                + "  <title>로그인 성공</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <script>\n"
                + "    // 서버에서 받은 토큰을 sessionStorage에 저장\n"
                + "    sessionStorage.setItem('accessToken', '" + serverJwt + "');\n"
                + "    // 홈 페이지로 리다이렉트\n"
                + "    window.location.href = '/';\n"
                + "  </script>\n"
                + "</body>\n"
                + "</html>";
        response.getWriter().write(htmlResponse);
    }
}
