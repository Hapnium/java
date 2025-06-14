package com.hapnium.core.resourcemanagement.cache;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.cache.providers.CaffeineCacheProvider;
import com.hapnium.core.resourcemanagement.cache.providers.RedisCacheProvider;
import com.hapnium.core.resourcemanagement.cache.providers.SimpleCacheProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Central configuration class for enabling and managing caching functionality.
 *
 * <p>This configuration class enables Spring AOP support and conditionally activates caching features based on the
 * property <code>hapnium.resourcemanagement.cache.enabled</code>.</p>
 *
 * <p>It scans for necessary beans including aspects and key generators to support annotation-based caching
 * such as {@code @Cacheable}, {@code @CacheEvict}, and {@code @CachePut}.</p>
 *
 * <p>Requires {@link ResourceManagementProperty} to be properly defined and registered as a configuration property.</p>
 *
 * <p><strong>Prerequisites:</strong></p>
 * <ul>
 *   <li>{@code ResourceManagementProperty} must be annotated with {@code @ConfigurationProperties}</li>
 *   <li>{@code @EnableConfigurationProperties(ResourceManagementProperty.class)} must be declared in any config</li>
 * </ul>
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@ConditionalOnProperty(name = "hapnium.resourcemanagement.cache.enabled", matchIfMissing = true)
public class ResourceManagementCacheManager {
    private final ResourceManagementProperty properties;

    private final String LOG_NAME = "[CACHE MANAGER]:";

    @Bean
    @Primary
    @ConditionalOnProperty(name = "hapnium.resourcemanagement.cache.provider", havingValue = "redis")
    public ResourceManagementCacheProvider redisCacheProvider(RedisTemplate<String, Object> redisTemplate) {
        log.info("{} Configuring Redis cache provider", LOG_NAME);
        return new RedisCacheProvider(redisTemplate, properties);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "hapnium.resourcemanagement.cache.provider", havingValue = "caffeine")
    public ResourceManagementCacheProvider caffeineCacheProvider() {
        log.info("{} Configuring Caffeine cache provider", LOG_NAME);
        return new CaffeineCacheProvider(properties);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ResourceManagementCacheProvider.class)
    public ResourceManagementCacheProvider defaultCacheProvider() {
        log.info("{} Configuring Simple in-memory cache provider as fallback", LOG_NAME);
        return new SimpleCacheProvider(properties.getCache().getKeyPrefix());
    }

    @Bean
    public ResourceManagementCacheService cacheService(ResourceManagementCacheProvider provider) {
        log.info("{} Initializing CacheService with provider: {}", LOG_NAME, provider.getClass().getSimpleName());
        return new ResourceManagementCacheService(provider, properties);
    }
}