package com.hapnium.core.resourcemanagement.rate_limit;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.rate_limit.providers.MemoryRateLimitProvider;
import com.hapnium.core.resourcemanagement.rate_limit.providers.RedisRateLimitProvider;
import com.hapnium.core.resourcemanagement.rate_limit.providers.SimpleRateLimitProvider;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Spring Boot configuration class responsible for initializing and managing rate limiting providers.
 * <p>
 * Based on configuration properties, this manager conditionally registers one of the supported
 * {@link ResourceManagementRateLimitProvider} implementations (Redis, Memory, or Simple).
 * </p>
 *
 * <p>Main Responsibilities:</p>
 * <ul>
 *   <li>Auto-detects and initializes the provider specified in the application properties</li>
 *   <li>Ensures graceful shutdown and resource cleanup</li>
 *   <li>Registers the core {@link ResourceManagementRateLimitService}</li>
 *   <li>Provides fallback options when no provider is explicitly configured</li>
 * </ul>
 *
 * <p>
 * To enable rate limiting, ensure that {@code hapnium.resourcemanagement.rate-limit.enabled=true}
 * and the appropriate provider-specific configurations are set.
 * </p>
 *
 * <p>
 * This class is also responsible for configuring the necessary beans such as the service layer and providers.
 * </p>
 *
 * @see MemoryRateLimitProvider
 * @see RedisRateLimitProvider
 * @see SimpleRateLimitProvider
 * @see ResourceManagementRateLimitService
 * @see ResourceManagementProperty
 * @see ResourceManagementRateLimitProvider
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
@ConditionalOnProperty(name = "hapnium.resourcemanagement.rate-limit.enabled", matchIfMissing = true)
public class ResourceManagementRateLimitManager {
    private final ResourceManagementProperty properties;
    private MemoryRateLimitProvider memoryProvider;

    private final String LOG_NAME = "[RATE LIMIT MANAGER]:";

    @Bean
    @Primary
    @ConditionalOnProperty(name = "hapnium.resourcemanagement.rate-limit.provider", havingValue = "redis")
    public ResourceManagementRateLimitProvider redisRateLimitProvider(RedisTemplate<String, Object> redisTemplate) {
        log.info("{} Configuring Redis rate limit provider", LOG_NAME);
        return new RedisRateLimitProvider(redisTemplate, properties);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "hapnium.resourcemanagement.rate-limit.provider", havingValue = "memory")
    public ResourceManagementRateLimitProvider memoryRateLimitProvider() {
        log.info("{} Configuring Memory rate limit provider", LOG_NAME);
        this.memoryProvider = new MemoryRateLimitProvider(properties);
        return this.memoryProvider;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ResourceManagementRateLimitProvider.class)
    public ResourceManagementRateLimitProvider defaultRateLimitProvider() {
        log.info("{} Configuring Simple in-memory rate limit provider as fallback", LOG_NAME);
        return new SimpleRateLimitProvider(properties.getRateLimit().getKeyPrefix());
    }

    @PreDestroy
    public void cleanup() {
        if (memoryProvider != null) {
            log.info("{} Shutting down memory rate limit provider", LOG_NAME);
            memoryProvider.shutdown();
        }
    }

    @Bean
    public ResourceManagementRateLimitService rateLimitService(ResourceManagementRateLimitProvider provider) {
        log.info("{} Initializing ResourceManagementRateLimitService with provider: {}", LOG_NAME, provider.getClass().getSimpleName());
        return new ResourceManagementRateLimitService(provider, properties);
    }
}