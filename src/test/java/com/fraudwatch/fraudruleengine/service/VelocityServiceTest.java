
package com.fraudwatch.fraudruleengine.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VelocityServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private VelocityService velocityService;

    @Test
    void testIncrementAndGet_ReturnsCorrectCount() {
        String key = "customer:123";
        long windowSecs = 60;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard("velocity:" + key)).thenReturn(5L);

        long result = velocityService.incrementAndGet(key, windowSecs);

        assertEquals(5, result);
        verify(zSetOperations, times(1)).removeRangeByScore(eq("velocity:" + key), anyDouble(), anyDouble());
        verify(zSetOperations, times(1)).add(eq("velocity:" + key), anyString(), anyDouble());
        verify(redisTemplate, times(1)).expire(eq("velocity:" + key), anyLong(), any());
    }

    @Test
    void testGetCount_ReturnsCorrectCount() {
        String key = "customer:456";
        long windowSecs = 300;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard("velocity:" + key)).thenReturn(3L);

        long result = velocityService.getCount(key, windowSecs);

        assertEquals(3, result);
        verify(zSetOperations, times(1)).removeRangeByScore(eq("velocity:" + key), anyDouble(), anyDouble());
    }

    @Test
    void testReset_DeletesKey() {
        String key = "customer:789";

        velocityService.reset(key);

        verify(redisTemplate, times(1)).delete("velocity:" + key);
    }
}
