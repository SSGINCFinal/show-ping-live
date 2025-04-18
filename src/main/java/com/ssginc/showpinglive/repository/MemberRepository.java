package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByMemberId(String memberId);
    boolean existsByMemberEmail(String memberEmail);
    Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByMemberEmail(String email);
    Member save(Member member);
}
