
package com.fraudwatch.fraudruleengine.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class VelocityService {

    private static final String VELOCITY_KEY_PREFIX = "velocity:";

    private final RedisTemplate<String, Object> redisTemplate;

    public VelocityService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Increments the velocity counter for a given key and window size
     * Uses Redis sorted sets for efficient sliding window implementation
     *
     * @param key        The velocity key (e.g., "customer:123:transactions")
     * @param windowSecs Window size in seconds
     * @return Current count in the window
     */
    public long incrementAndGet(String key, long windowSecs) {
        String redisKey = VELOCITY_KEY_PREFIX + key;
        long now = Instant.now().toEpochMilli();
        long windowStart = now - (windowSecs * 1000);

        // Remove old entries
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);

        // Add new entry with current timestamp as score
        redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);

        // Set TTL to window size plus some buffer
        redisTemplate.expire(redisKey, windowSecs + 60, TimeUnit.SECONDS);

        // Return count
        Long count = redisTemplate.opsForZSet().zCard(redisKey);
        return count != null ? count : 0;
    }

    /**
     * Gets the current count in the window without incrementing
     *
     * @param key        The velocity key
     * @param windowSecs Window size in seconds
     * @return Current count
     */
    public long getCount(String key, long windowSecs) {
        String redisKey = VELOCITY_KEY_PREFIX + key;
        long now = Instant.now().toEpochMilli();
        long windowStart = now - (windowSecs * 1000);

        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        Long count = redisTemplate.opsForZSet().zCard(redisKey);
        return count != null ? count : 0;
    }

    /**
     * Resets the velocity counter
     *
     * @param key The velocity key
     */
    public void reset(String key) {
        String redisKey = VELOCITY_KEY_PREFIX + key;
        redisTemplate.delete(redisKey);
    }
}
