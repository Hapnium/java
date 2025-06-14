package com.hapnium.core.resourcemanagement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * Configuration properties for Hapnium's caching and rate limiting systems.
 * <p>
 * These properties are prefixed with {@code hapnium} in the Spring Boot application configuration.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "hapnium.resourcemanagement")
public class ResourceManagementProperty {
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
}