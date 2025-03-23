package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member findMemberById(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + memberId));
    }

    @Override
    public Member findMember(String memberId, String password) {

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member != null) {
            if (!passwordEncoder.matches(password, member.getMemberPassword())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }
            else {
                return member;
            }
        } else {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        // 1. DB에서 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + memberId));

        // 2. UserDetails 객체 생성 후 반환
        return new org.springframework.security.core.userdetails.User(
                member.getMemberId(),   // 로그인 ID
                member.getMemberPassword(), // 암호화된 비밀번호
                Collections.singleton(new SimpleGrantedAuthority(member.getMemberRole().name())) // ROLE_ 접두사 추가
        );
    }

}