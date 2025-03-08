package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member findMemberById(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + memberId));
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