package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자 정보 조회
        Member member = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // Spring Security의 User 객체로 변환
        return User.builder()
                .username(member.getMemberId())
                .password(member.getMemberPassword()) // 비밀번호는 암호화된 상태여야 함 (BCrypt 등)
                .roles(member.getMemberRole().name()) // ROLE_USER, ROLE_ADMIN 등
                .build();
    }

    public Long getMemberNo(){
// SecurityContextHolder에서 현재    인증된 사용자의 username(=memberId) 가져오기
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        // memberId를 통해 DB에서 Member 엔티티 조회
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("memberId" + memberId);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId));

        return member.getMemberNo();
    }
}