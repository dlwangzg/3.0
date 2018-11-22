package com.leadingsoft.bizfuse.util.config;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "spring.redis.cache.expires")
public class CacheExpireConfig extends HashMap<String, Integer> {
    private static final long serialVersionUID = -8202251003845856803L;
}
