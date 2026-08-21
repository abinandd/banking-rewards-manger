package com.example.rewards.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's in-memory caching for short-lived, frequently read data such as
 * active reward rules and campaigns to eliminate N+1 DB queries per transaction in
 * the reward calculation engine.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String ACTIVE_RULES_CACHE = "activeRewardRules";
    public static final String ACTIVE_CAMPAIGNS_CACHE = "activeCampaigns";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(ACTIVE_RULES_CACHE, ACTIVE_CAMPAIGNS_CACHE);
    }
}
