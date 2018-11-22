package com.leadingsoft.bizfuse.util.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadingsoft.bizfuse.util.QueueManager;
import com.leadingsoft.bizfuse.util.impl.redis.QueueManagerRedisImpl;

@Configuration
public class StorageConfiguration extends CachingConfigurerSupport {

    @Autowired
    private CacheExpireConfig cacheExpireConfig;

    @Bean
    public CacheManager cacheManager(final RedisTemplate<String, Object> redisJsonTemplate) {
        final RedisCacheManager cacheManager = new RedisCacheManager(redisJsonTemplate);
        // Number of seconds before expiration. Defaults to unlimited (0)
        cacheManager.setDefaultExpiration(30 * 60); //设置key-value超时时间 默认30分
        if (this.cacheExpireConfig != null) {
            final Map<String, Long> expiresConfig = new HashMap<>();
            this.cacheExpireConfig.forEach((key, value) -> {
                expiresConfig.put(key, value.longValue());
            });
            cacheManager.setExpires(expiresConfig);
        }
        return cacheManager;
    }

    @Bean
    public QueueManager queueStorageManager(final RedisTemplate<String, Object> redisJsonTemplate) {
        return new QueueManagerRedisImpl(redisJsonTemplate);
    }

    @Bean
    public RedisTemplate<String, Object> redisJsonTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(connectionFactory);
        final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
        final ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        final RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (target, method, params) -> {
            final StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (final Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    public CacheExpireConfig cacheExpireConfig() {
        return new CacheExpireConfig();
    }
}
