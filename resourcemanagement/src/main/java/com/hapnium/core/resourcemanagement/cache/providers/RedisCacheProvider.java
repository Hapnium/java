package com.hapnium.core.resourcemanagement.cache.providers;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.cache.ResourceManagementCacheProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Redis-based implementation of the {@link ResourceManagementCacheProvider} interface.
 * <p>
 * This provider leverages Spring's {@link RedisTemplate} for interacting with a Redis backend to support
 * distributed caching. It allows storing and retrieving cache entries across multiple instances of an application,
 * making it suitable for horizontally scaled deployments.
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Distributed cache storage</li>
 *   <li>TTL (Time-To-Live) support via Redis expiration</li>
 *   <li>Pattern-based cache eviction capabilities</li>
 *   <li>Supports caching of complex Java objects using Redis serialization</li>
 * </ul>
 *
 * <p>Relies on Spring Boot's configuration for connecting to Redis.</p>
 *
 * @see RedisTemplate
 * @see ResourceManagementCacheProvider
 * @author Evaristus Adimonyemma
 */
@Slf4j
public class RedisCacheProvider implements ResourceManagementCacheProvider {
    /**
     * Redis template for performing key-value operations with Redis.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Centralized configuration properties for the resource management module.
     */
    private final ResourceManagementProperty properties;

    /**
     * Internal log name used to prefix log messages for easier traceability.
     */
    private final String LOG_NAME = "[REDIS_CACHE PROVIDER]:";

    /**
     * Retrieves cache-specific configuration properties.
     *
     * @return the cache-related configuration section
     */
    private ResourceManagementProperty.CacheProperties property() {
        return properties.getCache();
    }

    /**
     * Constructs the Redis cache provider with required dependencies.
     *
     * @param redisTemplate the Redis template used for cache operations
     * @param properties the configuration properties for cache behavior
     */
    public RedisCacheProvider(RedisTemplate<String, Object> redisTemplate, ResourceManagementProperty properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;

        log.info("{} Redis cache provider initialized", LOG_NAME);
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try {
            String fullKey = buildKey(key);
            if (ttl != null && !ttl.isZero()) {
                redisTemplate.opsForValue().set(fullKey, value, ttl);
            } else {
                redisTemplate.opsForValue().set(fullKey, value, property().getDefaultTtl());
            }
        } catch (Exception e) {
            log.error("{} Error putting value to Redis cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(buildKey(key));
            if (type.isInstance(value)) {
                return Optional.of(type.cast(value));
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("{} Error getting value from Redis cache: {}", LOG_NAME, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <T> CompletableFuture<Optional<T>> getAsync(String key, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> get(key, type));
    }

    @Override
    public <T> void putAsync(String key, T value, Duration ttl) {
        CompletableFuture.runAsync(() -> put(key, value, ttl));
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(buildKey(key));
        } catch (Exception e) {
            log.error("{} Error evicting value from Redis cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public void evictAll() {
        try {
            Set<String> keys = redisTemplate.keys(buildKey("*"));
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("{} Error evicting all values from Redis cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public void evictByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(buildKey("*" + pattern + "*"));
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("{} Error evicting values by pattern from Redis cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(buildKey(key));
        } catch (Exception e) {
            log.error("{} Error checking if key exists in Redis cache: {}", LOG_NAME, e.getMessage());
            return false;
        }
    }

    @Override
    public long size() {
        try {
            Set<String> keys = redisTemplate.keys(buildKey("*"));
            return keys.size();
        } catch (Exception e) {
            log.error("{} Error getting Redis cache size: {}", LOG_NAME, e.getMessage());
            return 0;
        }
    }

    @Override
    public void clear() {
        evictAll();
    }

    private String buildKey(String key) {
        return property().getRedis().getKeyPrefix() + key;
    }
}