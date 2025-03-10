package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.MemberDTO;
import com.ssginc.showpinglive.entity.Member;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

public interface AuthService {
    boolean login(Member member, HttpServletResponse response);

    @Transactional
    Member registerMember(MemberDTO memberDTO) throws Exception;

    String authenticate(String memberId, String password, RedirectAttributes redirectAttributes);

    void logout(HttpServletResponse response);

    Map<String, String> getUserInfo(Authentication authentication);

    boolean isDuplicateId(String memberId);

    Member findMemberById(String memberId);
}
