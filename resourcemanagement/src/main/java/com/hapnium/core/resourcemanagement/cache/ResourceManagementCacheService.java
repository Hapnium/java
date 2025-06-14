package com.hapnium.core.resourcemanagement.cache;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.cache.models.CacheRequest;
import com.hapnium.core.resourcemanagement.cache.models.CacheResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service layer for caching operations, using a pluggable {@link ResourceManagementCacheProvider}.
 * <p>
 * This service provides core functionality for interacting with the underlying cache system. It abstracts
 * common operations such as retrieving, storing, updating, and evicting cache entries.
 * </p>
 *
 * <p>Main Features:</p>
 * <ul>
 *   <li>Support for both synchronous and asynchronous cache operations</li>
 *   <li>Automatic handling of TTL (time-to-live) values</li>
 *   <li>Pattern-based cache eviction</li>
 *   <li>Customizable behavior via {@link CacheRequest} and {@link CacheResult}</li>
 * </ul>
 *
 * <p>
 * Typical use cases include integration with AOP-based caching annotations, manual cache control in services,
 * and centralized cache management across multiple cache backends.
 * </p>
 *
 * <p>Usage of this service assumes a properly configured {@link ResourceManagementCacheProvider} implementation.</p>
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceManagementCacheService {
    private final ResourceManagementCacheProvider cacheProvider;
    private final ResourceManagementProperty properties;

    private final String LOG_NAME = "[CACHE SERVICE]:";

    /**
     * Retrieves cache-related properties from the application's configuration.
     *
     * @return cache configuration properties
     */
    private ResourceManagementProperty.CacheProperties property() {
        return properties.getCache();
    }

    /**
     * Stores a value in the cache using the default TTL.
     *
     * @param key   the cache key
     * @param value the value to store
     * @param <T>   the type of value
     */
    public <T> void put(String key, T value) {
        put(key, value, property().getDefaultTtl());
    }

    /**
     * Stores a value in the cache with a specific TTL.
     *
     * @param key   the cache key
     * @param value the value to store
     * @param ttl   time-to-live duration
     * @param <T>   the type of value
     */
    public <T> void put(String key, T value, Duration ttl) {
        if (!property().isEnabled()) {
            log.debug("{} Cache is disabled, skipping put operation for key: {}", LOG_NAME, key);
            return;
        }

        try {
            cacheProvider.put(key, value, ttl);
            log.debug("{} Cached value for key: {}", LOG_NAME, key);
        } catch (Exception e) {
            log.error("{} Failed to cache value for key: {}", LOG_NAME, key, e);
        }
    }

    /**
     * Retrieves a value from the cache synchronously.
     *
     * @param key  the cache key
     * @param type the expected class of the value
     * @param <T>  the type of value
     * @return an optional containing the value if present
     */
    public <T> Optional<T> get(String key, Class<T> type) {
        if (!property().isEnabled()) {
            log.debug("{} Cache is disabled, returning empty for key: {}", LOG_NAME, key);
            return Optional.empty();
        }

        try {
            Optional<T> result = cacheProvider.get(key, type);
            log.debug("{} Cache {} for key: {}", LOG_NAME, result.isPresent() ? "hit" : "miss", key);
            return result;
        } catch (Exception e) {
            log.error("{} Failed to get cached value for key: {}", LOG_NAME, key, e);
            return Optional.empty();
        }
    }

    /**
     * Retrieves a value from the cache asynchronously.
     *
     * @param key  the cache key
     * @param type the expected class of the value
     * @param <T>  the type of value
     * @return a future containing the optional cached value
     */
    public <T> CompletableFuture<Optional<T>> getAsync(String key, Class<T> type) {
        if (!property().isEnabled()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return cacheProvider.getAsync(key, type)
                .exceptionally(throwable -> {
                    log.error("{} Failed to get cached value async for key: {}", LOG_NAME, key, throwable);
                    return Optional.empty();
                });
    }

    /**
     * Stores a value in the cache asynchronously with a TTL.
     *
     * @param key   the cache key
     * @param value the value to cache
     * @param ttl   time-to-live duration
     * @param <T>   the type of value
     */
    public <T> void putAsync(String key, T value, Duration ttl) {
        if (!property().isEnabled()) {
            return;
        }

        cacheProvider.putAsync(key, value, ttl);
    }

    /**
     * Removes a specific entry from the cache.
     *
     * @param key the cache key to remove
     */
    public void evict(String key) {
        if (!property().isEnabled()) {
            return;
        }

        try {
            cacheProvider.evict(key);
            log.debug("{} Evicted cache entry for key: {}", LOG_NAME, key);
        } catch (Exception e) {
            log.error("{} Failed to evict cache entry for key: {}", LOG_NAME, key, e);
        }
    }

    /**
     * Removes all entries from the cache.
     */
    public void evictAll() {
        if (!property().isEnabled()) {
            return;
        }

        try {
            cacheProvider.evictAll();
            log.info("{} Evicted all cache entries", LOG_NAME);
        } catch (Exception e) {
            log.error("{} Failed to evict all cache entries", LOG_NAME, e);
        }
    }

    /**
     * Evicts cache entries matching a given pattern.
     *
     * @param pattern the pattern to match keys
     */
    public void evictByPattern(String pattern) {
        if (!property().isEnabled()) {
            return;
        }

        try {
            cacheProvider.evictByPattern(pattern);
            log.debug("{} Evicted cache entries matching pattern: {}", LOG_NAME, pattern);
        } catch (Exception e) {
            log.error("{} Failed to evict cache entries by pattern: {}", LOG_NAME, pattern, e);
        }
    }

    /**
     * Checks if a cache entry exists for a given key.
     *
     * @param key the cache key
     * @return true if the key exists in the cache, false otherwise
     */
    public boolean exists(String key) {
        if (!property().isEnabled()) {
            return false;
        }

        try {
            return cacheProvider.exists(key);
        } catch (Exception e) {
            log.error("{} Failed to check if cache key exists: {}", LOG_NAME, key, e);
            return false;
        }
    }

    /**
     * Returns the total number of entries in the cache.
     *
     * @return the size of the cache
     */
    public long size() {
        if (!property().isEnabled()) {
            return 0;
        }

        try {
            return cacheProvider.size();
        } catch (Exception e) {
            log.error("{} Failed to get cache size", LOG_NAME, e);
            return 0;
        }
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        if (!property().isEnabled()) {
            return;
        }

        try {
            cacheProvider.clear();
            log.info("{} Cleared all cache entries", LOG_NAME);
        } catch (Exception e) {
            log.error("{} Failed to clear cache", LOG_NAME, e);
        }
    }
}