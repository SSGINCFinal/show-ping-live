package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.entity.SocialMember;
import com.ssginc.showpinglive.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialMemberRepository extends JpaRepository<SocialMember, Long> {
    Optional<SocialMember> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
