package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.entity.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MemberService extends UserDetailsService {

    Member findMemberById(String memberId);

    UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException;

}
