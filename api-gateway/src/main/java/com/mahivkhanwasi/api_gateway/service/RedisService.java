package com.mahivkhanwasi.api_gateway.service;

import com.mahivkhanwasi.api_gateway.dto.RedisUserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

//    public void saveUserSession(String fullName, RedisUserSession userSession) {
//        redisTemplate.opsForValue().set(fullName, userSession, Duration.ofHours(2)); // Expires in 2 hours
//    }

    public RedisUserSession getUserSession(String fullName) {
        return (RedisUserSession) redisTemplate.opsForValue().get(fullName);
    }


    public boolean isUserInRedis(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(username));
    }
}
