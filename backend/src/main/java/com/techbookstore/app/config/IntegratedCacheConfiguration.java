package com.techbookstore.app.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache configuration for Phase 4 integrated analysis
 * Provides different TTL configurations for different analysis types
 * Uses Redis when available, falls back to simple caching for tests
 */
@Configuration
@EnableCaching
public class IntegratedCacheConfiguration {
    
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
            .builder(connectionFactory)
            .cacheDefaults(cacheConfiguration());
        
        // Phase 4 integrated cache configurations with different TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Base inventory reports (Phase 1) - 5 minutes cache
        cacheConfigurations.put("baseInventoryReport", 
            cacheConfiguration().entryTtl(Duration.ofMinutes(5)));
        
        // Advanced analysis (Phase 2) - 15 minutes cache
        cacheConfigurations.put("advancedAnalysis", 
            cacheConfiguration().entryTtl(Duration.ofMinutes(15)));
        
        // Forecast analysis (Phase 3) - 30 minutes cache
        cacheConfigurations.put("forecastAnalysis", 
            cacheConfiguration().entryTtl(Duration.ofMinutes(30)));
        
        // Integrated analysis (Phase 4) - 10 minutes cache
        cacheConfigurations.put("integratedAnalysis", 
            cacheConfiguration().entryTtl(Duration.ofMinutes(10)));
        
        // Performance metrics - 2 minutes cache
        cacheConfigurations.put("performanceMetrics", 
            cacheConfiguration().entryTtl(Duration.ofMinutes(2)));
        
        // Dashboard data - 1 minute cache for real-time feel
        cacheConfigurations.put("dashboardData", 
            cacheConfiguration().entryTtl(Duration.ofMinutes(1)));
        
        return builder.withInitialCacheConfigurations(cacheConfigurations).build();
    }
    
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "simple")
    public CacheManager simpleCacheManager() {
        // Simple cache manager for test environments
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "baseInventoryReport",
            "advancedAnalysis", 
            "forecastAnalysis",
            "integratedAnalysis",
            "performanceMetrics",
            "dashboardData"
        ));
        return cacheManager;
    }
    
    private RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()
                    .configure(mapper -> mapper.registerModule(new JavaTimeModule()))))
            .disableCachingNullValues();
    }
}