package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

public interface MemberService extends UserDetailsService {

    Member findMemberById(String memberId);
    Member findMember(String memberId, String password);

    UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException;

}
