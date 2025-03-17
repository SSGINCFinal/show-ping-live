package com.ssginc.showpinglive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

public interface RefreshTokenService {

    void saveRefreshToken(String username, String refreshToken);

    String getRefreshToken(String username);

    void deleteRefreshToken(String username);

    String checkRefreshToken(String username);
}