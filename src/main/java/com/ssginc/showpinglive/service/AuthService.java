package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.MemberDto;
import com.ssginc.showpinglive.entity.Member;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

public interface AuthService {
    ResponseEntity<?> login(Member member, HttpServletResponse response);

    @Transactional
    Member registerMember(MemberDto memberDTO) throws Exception;

    @Transactional
    String authenticate(String memberId, String password, RedirectAttributes redirectAttributes);

    void logout(String username, HttpServletResponse response);

    boolean isDuplicateId(String memberId);

    boolean isDuplicateEmail(String memberEmail);

    Member findMemberById(String memberId);

    ResponseEntity<Map<String, String>> verifyTOTP(String memberId, int totpCode);

    boolean verifyPassword(String memberId, String password);
}
