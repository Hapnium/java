package com.hapnium.core.resourcemanagement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.util.Map;

/**
 * Configuration properties for Hapnium's caching and rate limiting systems.
 * <p>
 * These properties are prefixed with {@code hapnium} in the Spring Boot application configuration.
 */
@Getter
@Setter
@Lazy(false)
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "hapnium.resourcemanagement")
public class ResourceManagementProperty implements InitializingBean {
    /**
     * Cache-related configuration properties.
     */
    private CacheProperties cache = new CacheProperties();

    /**
     * Rate limiting-related configuration properties.
     */
    private RateLimitProperties rateLimit = new RateLimitProperties();

    /**
     * Cache configuration including general settings and provider-specific options.
     */
    @Getter
    @Setter
    public static class CacheProperties {
        /**
         * Whether caching is enabled.
         */
        private boolean enabled = true;

        /**
         * The caching provider to use: {@code caffeine}, {@code redis}, or {@code memory}.
         */
        private String provider = "caffeine";

        /**
         * Default time-to-live (TTL) for cache entries.
         */
        private Duration defaultTtl = Duration.ofMinutes(5);

        /**
         * Prefix for all cache keys.
         */
        private String keyPrefix = "cache:";

        /**
         * Whether to enable a metrics collection for the cache.
         */
        private boolean enableMetrics = true;

        /**
         * Whether to enable internal cache statistics (hit/miss ratio, etc.).
         */
        private boolean enableStatistics = true;

        /**
         * Configuration for named caches.
         * The key is the cache name.
         */
        private Map<String, CacheConfigProperties> caches;

        /**
         * Redis-specific caching configuration.
         */
        private RedisProperties redis = new RedisProperties();

        /**
         * Caffeine-specific caching configuration.
         */
        private CaffeineProperties caffeine = new CaffeineProperties();
    }

    /**
     * Configuration for individual named caches.
     */
    @Getter
    @Setter
    public static class CacheConfigProperties {
        /**
         * Time-to-live (TTL) for cache entries in this cache.
         */
        private Duration ttl;

        /**
         * Maximum number of entries allowed in this cache.
         */
        private int maxSize;

        /**
         * Whether to enable automatic refresh of cache entries.
         */
        private boolean enableRefresh;

        /**
         * Duration after which a cache entry will be refreshed (if enabled).
         */
        private Duration refreshAfterWrite;
    }

    /**
     * Caffeine cache-specific configuration.
     */
    @Getter
    @Setter
    public static class CaffeineProperties {
        /**
         * Maximum number of entries in the Caffeine cache.
         */
        private int maximumSize = 10000;

        /**
         * Duration after which an entry expires after it is written.
         */
        private Duration expireAfterWrite = Duration.ofMinutes(10);

        /**
         * Duration after which an entry expires after last access.
         */
        private Duration expireAfterAccess = Duration.ofMinutes(5);

        /**
         * Whether to enable Caffeine's statistics.
         */
        private boolean enableStatistics = true;

        /**
         * Whether to record Caffeine stats for analysis.
         */
        private boolean recordStats = true;
    }

    /**
     * Rate limiting configuration properties.
     */
    @Getter
    @Setter
    public static class RateLimitProperties {
        /**
         * Whether rate limiting is enabled.
         */
        private boolean enabled = true;

        /**
         * Provider used for storing rate limit data: {@code memory}, {@code redis}.
         */
        private String provider = "memory";

        /**
         * Default rate limiting strategy used: {@code sliding-window}, {@code token-bucket}, or {@code fixed-window}.
         */
        private String defaultStrategy = "sliding-window";

        /**
         * Default request limit per window.
         */
        private int defaultLimit = 100;

        /**
         * Default time window for rate limiting.
         */
        private Duration defaultWindow = Duration.ofMinutes(1);

        /**
         * Whether to skip rate limiting if an internal error occurs.
         */
        private boolean skipOnFailure = true;

        /**
         * Prefix used for all rate limit keys.
         */
        private String keyPrefix = "rl:";

        /**
         * Endpoint-specific rate limit configurations.
         * The key is the endpoint path.
         */
        private Map<String, EndpointProperties> endpoints;

        /**
         * User-type-specific rate limit configurations.
         * The key is the user type name.
         */
        private Map<String, UserTypeProperties> userTypes;

        /**
         * Redis-specific rate limit configuration.
         */
        private RedisProperties redis = new RedisProperties();

        /**
         * In-memory rate limit configuration.
         */
        private MemoryProperties memory = new MemoryProperties();
    }

    /**
     * Rate limit configuration for specific API endpoints.
     */
    @Getter
    @Setter
    public static class EndpointProperties {
        /**
         * Whether rate limiting is enabled for this endpoint.
         */
        private boolean enabled = true;

        /**
         * Maximum number of requests allowed in the time window.
         */
        private int limit;

        /**
         * Time window for rate limiting.
         */
        private Duration window;

        /**
         * Strategy used for this endpoint: {@code sliding-window}, {@code token-bucket}, or {@code fixed-window}.
         */
        private String strategy;

        /**
         * Per-user-type request limits.
         */
        private Map<String, Integer> userTypeLimits;

        /**
         * Whether to skip rate limiting for authenticated users.
         */
        private Boolean skipAuthenticated;
    }

    /**
     * Rate limit configuration for specific user types.
     */
    @Getter
    @Setter
    public static class UserTypeProperties {
        /**
         * Maximum number of requests allowed.
         */
        private int limit;

        /**
         * Time window for rate limiting.
         */
        private Duration window;

        /**
         * Rate limit strategy: {@code sliding-window}, {@code token-bucket}, or {@code fixed-window}.
         */
        private String strategy;

        /**
         * Whether rate limiting is enabled for this user type.
         */
        private boolean enabled = true;
    }

    /**
     * Common Redis-related configuration used in caching and rate limiting.
     */
    @Getter
    @Setter
    public static class RedisProperties {
        /**
         * Prefix used for all Redis keys.
         */
        private String keyPrefix = "";

        /**
         * Default expiration time for Redis keys.
         */
        private Duration keyExpiration = Duration.ofHours(1);

        /**
         * Maximum number of retry attempts for Redis operations.
         */
        private int maxRetries = 3;

        /**
         * Delay between Redis operation retry attempts.
         */
        private Duration retryDelay = Duration.ofMillis(100);

        /**
         * Whether to enable compression of Redis values.
         */
        private boolean enableCompression = false;

        /**
         * Minimum value size (in bytes) required before compression is applied.
         */
        private int compressionThreshold = 1024;
    }

    /**
     * In-memory rate limiting configuration.
     */
    @Getter
    @Setter
    public static class MemoryProperties {
        /**
         * Maximum number of entries in the in-memory store.
         */
        private int maxEntries = 10000;

        /**
         * Interval between periodic cleanup of stale entries.
         */
        private Duration cleanupInterval = Duration.ofMinutes(5);

        /**
         * Whether to enable a metrics collection for in-memory rate limiting.
         */
        private boolean enableMetrics = true;
    }

    @Override
    public void afterPropertiesSet() {
        // Validate Cache Properties
        if (cache.getProvider() == null || cache.getProvider().isBlank()) {
            cache.setProvider("caffeine");
        }

        if (cache.getDefaultTtl() == null) {
            cache.setDefaultTtl(Duration.ofMinutes(5));
        }

        if (cache.getKeyPrefix() == null) {
            cache.setKeyPrefix("cache:");
        }

        if (cache.getCaffeine() != null) {
            if (cache.getCaffeine().getExpireAfterWrite() == null) {
                cache.getCaffeine().setExpireAfterWrite(Duration.ofMinutes(10));
            }
            if (cache.getCaffeine().getExpireAfterAccess() == null) {
                cache.getCaffeine().setExpireAfterAccess(Duration.ofMinutes(5));
            }
        }

        if (cache.getRedis() != null) {
            if (cache.getRedis().getKeyExpiration() == null) {
                cache.getRedis().setKeyExpiration(Duration.ofHours(1));
            }
            if (cache.getRedis().getRetryDelay() == null) {
                cache.getRedis().setRetryDelay(Duration.ofMillis(100));
            }
        }

        // Validate Rate Limit Properties
        if (rateLimit.getProvider() == null || rateLimit.getProvider().isBlank()) {
            rateLimit.setProvider("memory");
        }

        if (rateLimit.getDefaultStrategy() == null || rateLimit.getDefaultStrategy().isBlank()) {
            rateLimit.setDefaultStrategy("sliding-window");
        }

        if (rateLimit.getDefaultWindow() == null) {
            rateLimit.setDefaultWindow(Duration.ofMinutes(1));
        }

        if (rateLimit.getKeyPrefix() == null) {
            rateLimit.setKeyPrefix("rl:");
        }

        if (rateLimit.getRedis() != null) {
            if (rateLimit.getRedis().getKeyExpiration() == null) {
                rateLimit.getRedis().setKeyExpiration(Duration.ofHours(1));
            }
            if (rateLimit.getRedis().getRetryDelay() == null) {
                rateLimit.getRedis().setRetryDelay(Duration.ofMillis(100));
            }
        }

        if (rateLimit.getMemory() != null) {
            if (rateLimit.getMemory().getCleanupInterval() == null) {
                rateLimit.getMemory().setCleanupInterval(Duration.ofMinutes(5));
            }
        }

        // Endpoint-level defaults
        if (rateLimit.getEndpoints() != null) {
            for (Map.Entry<String, EndpointProperties> entry : rateLimit.getEndpoints().entrySet()) {
                EndpointProperties ep = entry.getValue();

                if (ep.getWindow() == null) {
                    ep.setWindow(rateLimit.getDefaultWindow());
                }
                if (ep.getStrategy() == null || ep.getStrategy().isBlank()) {
                    ep.setStrategy(rateLimit.getDefaultStrategy());
                }
            }
        }

        // User-type-level defaults
        if (rateLimit.getUserTypes() != null) {
            for (Map.Entry<String, UserTypeProperties> entry : rateLimit.getUserTypes().entrySet()) {
                UserTypeProperties ut = entry.getValue();

                if (ut.getWindow() == null) {
                    ut.setWindow(rateLimit.getDefaultWindow());
                }
                if (ut.getStrategy() == null || ut.getStrategy().isBlank()) {
                    ut.setStrategy(rateLimit.getDefaultStrategy());
                }
            }
        }

        // Cache named configurations
        if (cache.getCaches() != null) {
            for (Map.Entry<String, CacheConfigProperties> entry : cache.getCaches().entrySet()) {
                CacheConfigProperties c = entry.getValue();

                if (c.getTtl() == null) {
                    c.setTtl(cache.getDefaultTtl());
                }
            }
        }
    }
}