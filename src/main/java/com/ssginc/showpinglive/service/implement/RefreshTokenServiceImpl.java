package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRATION = 86400000L; // 24시간

    // Refresh Token 저장
    public void saveRefreshToken(String username, String refreshToken) {

        System.out.println("refreshToken 저장 단계 : \"" + username +"\"");
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set("refreshToken:" + username, refreshToken, REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public String getRefreshToken(String username) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        return (String) valueOps.get("refreshToken:" + username);
    }

    // Refresh Token 삭제 (로그아웃 시 사용)
    public void deleteRefreshToken(String username) {
        System.out.println("Redis에서 Refresh Token 삭제 요청: " + username);
        redisTemplate.delete("refreshToken:" + username);
        System.out.println("Redis에서 Refresh Token 삭제 완료!");
    }

    public String checkRefreshToken(String username) {
        String token = getRefreshToken(username);
        System.out.println("Redis에 저장된 Refresh Token: " + token);
        return token;
    }
}
