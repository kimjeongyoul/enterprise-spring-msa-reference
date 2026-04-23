package com.global.auth.common.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisIdempotencyManager implements IdempotencyManager {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "idempotency:";
    private static final Duration DEFAULT_TTL = Duration.ofDays(1); // 기본 1일 보관

    @Override
    public boolean isProcessed(String key) {
        // SET if Not eXists
        Boolean success = redisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + key, "processing", DEFAULT_TTL);
        return success == null || !success;
    }

    @Override
    public void markAsProcessed(String key) {
        redisTemplate.opsForValue().set(KEY_PREFIX + key, "completed", DEFAULT_TTL);
    }
}
