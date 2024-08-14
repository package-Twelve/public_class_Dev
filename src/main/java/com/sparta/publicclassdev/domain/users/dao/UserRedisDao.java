package com.sparta.publicclassdev.domain.users.dao;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRedisDao {
    private final RedisTemplate<String, String> redisTemplate;

    public void setRefreshToken(String key, String refreshToken, long refreshTokenTime) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(refreshToken.getClass()));
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenTime, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setBlackList(String accessToken, String msg, Long milliseconds) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(msg.getClass()));
        redisTemplate.opsForValue().set(accessToken, msg, milliseconds, TimeUnit.MILLISECONDS);
    }

    public String getBlackList(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public void flushAll(){
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
