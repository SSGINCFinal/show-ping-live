package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.service.AuthService;
import com.ssginc.showpinglive.service.implement.AuthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public String login(Member member, HttpServletResponse response, Model model) {
        boolean loginSuccess = authService.login(member, response);

        if (!loginSuccess) {
            model.addAttribute("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login/login";  // 로그인 페이지로 다시 이동
        }

        return "redirect:/";  // 로그인 성공 시 홈으로 이동
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.logout(response); // ✅ 서비스에서 로그아웃 처리
        return "redirect:/";  // 홈페이지로 이동
    }

    @GetMapping("/user-info3")
    @ResponseBody
    public Map<String, String> getUserInfo(Authentication authentication) {
        return authService.getUserInfo(authentication); // ✅ 서비스에서 사용자 정보 조회
    }

}

