package com.hapnium.core.resourcemanagement.cache.providers;

import com.hapnium.core.resourcemanagement.cache.ResourceManagementCacheProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple in-memory implementation of the {@link ResourceManagementCacheProvider} interface.
 * <p>
 * This cache provider uses a thread-safe {@link ConcurrentHashMap} to store key-value pairs
 * in memory and is intended for local, non-distributed caching scenarios, such as testing or
 * single-node applications.
 * </p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Lightweight and fast local caching</li>
 *   <li>Thread-safe access using {@link ConcurrentHashMap}</li>
 *   <li>Basic key prefixing support</li>
 *   <li>No TTL (Time-To-Live) or expiration support</li>
 * </ul>
 *
 * <p><strong>Limitations:</strong></p>
 * <ul>
 *   <li>Not suitable for distributed environments</li>
 *   <li>No native support for TTL or eviction policies</li>
 *   <li>Memory usage is not bounded</li>
 * </ul>
 *
 * @see ResourceManagementCacheProvider
 * @author Evaristus Adimonyemma
 */
@Slf4j
public class SimpleCacheProvider implements ResourceManagementCacheProvider {
    /**
     * Thread-safe in-memory storage for cache entries.
     */
    private final ConcurrentMap<String, Object> cache = new ConcurrentHashMap<>();

    /**
     * Optional prefix to add to all cache keys, useful for namespacing.
     */
    private final String keyPrefix;

    /**
     * Prefix used for logging to distinguish messages from this provider.
     */
    private final String LOG_NAME = "[SIMPLE_CACHE PROVIDER]:";

    /**
     * Constructs a new {@code SimpleCacheProvider} with a specified key prefix.
     *
     * @param keyPrefix the prefix to prepend to all keys; if {@code null}, a default value of {@code "cache:"} is used
     */
    public SimpleCacheProvider(String keyPrefix) {
        this.keyPrefix = keyPrefix != null ? keyPrefix : "cache:";
        log.info("{} Simple in-memory cache provider initialized", LOG_NAME);
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try {
            // Simple implementation ignores TTL
            cache.put(buildKey(key), value);
        } catch (Exception e) {
            log.error("{} Error putting value to cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = cache.get(buildKey(key));
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
            cache.remove(buildKey(key));
        } catch (Exception e) {
            log.error("{} Error evicting value from cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public void evictAll() {
        try {
            cache.clear();
        } catch (Exception e) {
            log.error("{} Error evicting all values from cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public void evictByPattern(String pattern) {
        try {
            cache.entrySet().removeIf(entry -> entry.getKey().contains(pattern));
        } catch (Exception e) {
            log.error("{} Error evicting values by pattern from cache: {}", LOG_NAME, e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return cache.containsKey(buildKey(key));
        } catch (Exception e) {
            log.error("{} Error checking if key exists in cache: {}", LOG_NAME, e.getMessage());
            return false;
        }
    }

    @Override
    public long size() {
        try {
            return cache.size();
        } catch (Exception e) {
            log.error("{} Error getting cache size: {}", LOG_NAME, e.getMessage());
            return 0;
        }
    }

    @Override
    public void clear() {
        evictAll();
    }

    private @NonNull String buildKey(String key) {
        return keyPrefix + key;
    }
}