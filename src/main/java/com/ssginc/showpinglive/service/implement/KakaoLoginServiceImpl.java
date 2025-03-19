//package com.ssginc.showpinglive.service.implement;
//
//import com.ssginc.showpinglive.dto.object.KakaoUserInfo;
//import com.ssginc.showpinglive.dto.response.KakaoTokenResponse;
//import com.ssginc.showpinglive.entity.Member;
//import com.ssginc.showpinglive.entity.MemberRole;
//import com.ssginc.showpinglive.entity.SocialMember;
//import com.ssginc.showpinglive.entity.SocialType;
//import com.ssginc.showpinglive.jwt.JwtUtil;
//import com.ssginc.showpinglive.repository.MemberRepository;
//import com.ssginc.showpinglive.repository.SocialMemberRepository;
//import com.ssginc.showpinglive.service.KakaoLoginService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class KakaoLoginServiceImpl implements KakaoLoginService {
//    private final MemberRepository memberRepository;
//    private final SocialMemberRepository socialMemberRepository;
//    private final JwtUtil jwtUtil;
//    private final RestTemplate restTemplate;
//    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
//
//    public String kakaoLogin(String code) {
//        // 1. 인가 코드를 통해 access token 발급
//        String accessToken = getKakaoAccessToken(code);
//        // 2. access token으로 카카오 사용자 정보 조회
//        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(accessToken);
//
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        System.out.println("KakaoUserInfo: " + kakaoUserInfo);
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//        String kakaoId = String.valueOf(kakaoUserInfo.getId());
//        Optional<SocialMember> optionalSocialMember = socialMemberRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, kakaoId);
//        Member member;
//        if (optionalSocialMember.isPresent()) {
//            member = optionalSocialMember.get().getMember();
//        } else {
//            // 이미 등록된 이메일이 있는지 확인
//            String email = kakaoUserInfo.getKakaoAccount().getEmail();
//            Optional<Member> existingMember = memberRepository.findByMemberEmail(email);
//            if (existingMember.isPresent()) {
//                member = existingMember.get();
//            } else {
//                // 신규 회원 생성
//                member = createNewMember(kakaoUserInfo);
//            }
//            // 소셜 회원 정보 저장
//            createNewSocialMember(member, kakaoUserInfo);
//        }
//        // JWT 생성 후 반환
//        return  jwtUtil.generateAccessToken(String.valueOf(member.getMemberNo()), member.getMemberRole().toString());
//    }
//
//    private String getKakaoAccessToken(String code) {
//        String tokenUrl = "https://kauth.kakao.com/oauth/token";
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", "08359b4d82988a73ec2caa499b9744c1");
//        params.add("client_secret", "TRxG6rU2URFZpKOwSAiQaSfPW28RUO76");
//        params.add("redirect_uri", "http://localhost:8080/oauth/kakao/callback");
//        params.add("code", code);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, KakaoTokenResponse.class);
//        return response.getBody().getAccessToken();
//    }
//
//    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
//        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, KakaoUserInfo.class);
//        return response.getBody();
//    }
//
//
//    private Member createNewMember(KakaoUserInfo kakaoUserInfo) {
//        String randomPassword = UUID.randomUUID().toString();
//        return memberRepository.save(
//                Member.builder()
//                        .memberId("kakao_" + kakaoUserInfo.getId())
//                        .memberPassword(passwordEncoder.encode(randomPassword))
//                        .memberName(kakaoUserInfo.getProperties().getNickname())
//                        .memberEmail(kakaoUserInfo.getKakaoAccount().getEmail())
//                        .memberRole(MemberRole.ROLE_USER)
//                        .streamKey(UUID.randomUUID().toString())
//                        .memberAddress("소셜회원")
//                        .build()
//        );
//    }
//
//    private void createNewSocialMember(Member member, KakaoUserInfo kakaoUserInfo) {
//        SocialMember socialMember = SocialMember.builder()
//                .member(member)
//                .socialType(SocialType.KAKAO)
//                .socialId(String.valueOf(kakaoUserInfo.getId()))
//                .socialEmail(kakaoUserInfo.getKakaoAccount().getEmail())
//                .build();
//        socialMemberRepository.save(socialMember);
//    }
//}
