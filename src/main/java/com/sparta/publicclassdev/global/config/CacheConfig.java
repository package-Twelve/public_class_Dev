package com.sparta.publicclassdev.global.config;

import com.sparta.publicclassdev.global.CacheNames;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ResourceLoader resourceLoader) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext
                    .SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer())
            );
        
        Map<String, RedisCacheConfiguration> redisCacheConfigMap
            = new HashMap<>();

        redisCacheConfigMap.put(
            CacheNames.USERBYEMAIL,
            defaultConfig.entryTtl(Duration.ofMinutes(30))
        );
        
        redisCacheConfigMap.put(
            CacheNames.ALLUSERS,
            defaultConfig.entryTtl(Duration.ofHours(4))
                .serializeValuesWith(
                    RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new JdkSerializationRedisSerializer())
                )
        );
        redisCacheConfigMap.put(
            CacheNames.LOGINUSER,
            defaultConfig.entryTtl(Duration.ofHours(2))
        );


        return RedisCacheManager.builder(connectionFactory)
            .withInitialCacheConfigurations(redisCacheConfigMap)
            .build();
    }
}
