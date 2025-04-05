package com.mahivkhanwasi.api_gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // Set key-value pair in Redis (override if exists)
    public void saveToken(String fullName, String token) {
        redisTemplate.opsForValue().set(fullName, token, Duration.ofHours(2)); // Expires in 2 hours
    }

    // Get token by full name
    public String getToken(String fullName) {
        return redisTemplate.opsForValue().get(fullName);
    }


    public boolean isUserInRedis(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(username));
    }
}
