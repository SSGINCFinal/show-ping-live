package com.ssginc.showpinglive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


public interface RedisTestService {

    void testRedis();
}
