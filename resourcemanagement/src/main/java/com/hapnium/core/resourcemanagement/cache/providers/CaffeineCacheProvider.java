package com.hapnium.core.resourcemanagement.cache.providers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.cache.ResourceManagementCacheProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A cache provider implementation backed by the Caffeine in-memory caching library.
 * <p>
 * Supports TTL, size limits, access/write expiration, and optional statistics recording.
 * Designed for high-performance local caching in Java applications.
 * <p>
 * Caches are stored in a concurrent map, keyed by their name, allowing for flexible cache partitioning.
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Per-cache configuration via {@link ResourceManagementProperty.CaffeineProperties}</li>
 *   <li>Support for expiration after access or write</li>
 *   <li>Maximum size limitation</li>
 *   <li>Optional cache statistics recording</li>
 * </ul>
 *
 * <p>This implementation is non-distributed and best suited for local, single-node use cases.</p>
 *
 * @see com.github.benmanes.caffeine.cache.Caffeine
 * @see ResourceManagementCacheProvider
 * @author Evaristus Adimonyemma
 */
@Slf4j
public class CaffeineCacheProvider implements ResourceManagementCacheProvider {
    /**
     * Map of named caches for reuse and separation of cache segments.
     */
    private final ConcurrentMap<String, Cache<String, Object>> caches = new ConcurrentHashMap<>();

    /**
     * Configuration properties for resource management and caching.
     */
    private final ResourceManagementProperty properties;

    /**
     * Default cache instance initialized from configuration.
     */
    private final Cache<String, Object> defaultCache;

    /**
     * Default cache instance initialized from configuration.
     */
    private final String LOG_NAME = "[CAFFEINE_CACHE PROVIDER]:";

    /**
     * Utility method to retrieve cache-specific properties from the global configuration.
     *
     * @return the {@link ResourceManagementProperty.CacheProperties} object
     */
    private ResourceManagementProperty.CacheProperties property() {
        return properties.getCache();
    }

    /**
     * Constructs the {@code CaffeineCacheProvider} with the given configuration properties,
     * and initializes the default cache using Caffeine.
     *
     * @param properties the configuration object containing Caffeine-specific settings
     */
    public CaffeineCacheProvider(@NonNull ResourceManagementProperty properties) {
        this.properties = properties;
        this.defaultCache = createCache(properties.getCache().getCaffeine());

        log.info("{} Caffeine cache provider initialized with default cache", LOG_NAME);
    }

    /**
     * Creates a Caffeine cache instance based on the given configuration properties.
     *
     * @param props the configuration object for Caffeine cache
     * @return a configured Caffeine {@link Cache} instance
     */
    private @NonNull Cache<String, Object> createCache(@NonNull ResourceManagementProperty.CaffeineProperties props) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        
        if (props.getMaximumSize() > 0) {
            builder.maximumSize(props.getMaximumSize());
        }
        
        if (props.getExpireAfterWrite() != null) {
            builder.expireAfterWrite(props.getExpireAfterWrite());
        }
        
        if (props.getExpireAfterAccess() != null) {
            builder.expireAfterAccess(props.getExpireAfterAccess());
        }
        
        if (props.isRecordStats()) {
            builder.recordStats();
        }
        
        return builder.build();
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try {
            // Caffeine doesn't support per-key TTL, so we ignore the TTL parameter
            defaultCache.put(buildKey(key), value);
        } catch (Exception e) {
            log.error("{} Error putting value to cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = defaultCache.getIfPresent(buildKey(key));
            if (type.isInstance(value)) {
                return Optional.of(type.cast(value));
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("{} Error getting value from cache: {}", LOG_NAME, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <T> CompletableFuture<Optional<T>> getAsync(String key, Class<T> type) {
        return CompletableFuture.completedFuture(get(key, type));
    }

    @Override
    public <T> void putAsync(String key, T value, Duration ttl) {
        CompletableFuture.runAsync(() -> put(key, value, ttl));
    }

    @Override
    public void evict(String key) {
        try {
            defaultCache.invalidate(buildKey(key));
        } catch (Exception e) {
            log.error("{} Error evicting value from cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public void evictAll() {
        try {
            defaultCache.invalidateAll();
            caches.values().forEach(Cache::invalidateAll);
        } catch (Exception e) {
            log.error("{} Error evicting all values from cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public void evictByPattern(String pattern) {
        try {
            // Simple pattern matching - check if key contains a pattern
            defaultCache.asMap().keySet().removeIf(key -> key.contains(pattern));
            caches.values().forEach(cache -> 
                cache.asMap().keySet().removeIf(key -> key.contains(pattern))
            );
        } catch (Exception e) {
            log.error("{} Error evicting values by pattern from cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return defaultCache.getIfPresent(buildKey(key)) != null;
        } catch (Exception e) {
            log.error("{} Error checking if key exists in cache: {}", LOG_NAME, e.getMessage());
            return false;
        }
    }

    @Override
    public long size() {
        try {
            long totalSize = defaultCache.estimatedSize();
            for (Cache<String, Object> cache : caches.values()) {
                totalSize += cache.estimatedSize();
            }
            return totalSize;
        } catch (Exception e) {
            log.error("{} Error getting cache size: {}", LOG_NAME, e.getMessage());
            return 0;
        }
    }

    @Override
    public void clear() {
        try {
            defaultCache.invalidateAll();
            caches.values().forEach(Cache::invalidateAll);
            caches.clear();
        } catch (Exception e) {
            log.error("{} Error clearing cache: {}", LOG_NAME, e.getMessage());
        }
    }

    private @NonNull String buildKey(String key) {
        return property().getKeyPrefix() + key;
    }
}