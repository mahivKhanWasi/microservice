package com.mahivkhanwasi.auth_service.service;

import com.mahivkhanwasi.auth_service.dto.RedisUserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveUserSession(String fullName, RedisUserSession userSession) {
        redisTemplate.opsForValue().set(fullName, userSession, Duration.ofHours(2)); // Expires in 2 hours
    }

    public RedisUserSession getUserSession(String fullName) {
        return (RedisUserSession) redisTemplate.opsForValue().get(fullName);
    }

    public void deleteUserSession(String fullName) {
        redisTemplate.delete(fullName);
    }
}