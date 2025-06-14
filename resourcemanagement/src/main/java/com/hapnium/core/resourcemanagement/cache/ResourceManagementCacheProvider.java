package com.hapnium.core.resourcemanagement.cache;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Defines a contract for a generic cache provider that supports various caching operations including
 * synchronous and asynchronous access, TTL-based entry expiration, key-based eviction, and pattern matching.
 * <p>
 * Implementations may use different backing stores such as Redis, Caffeine, or in-memory maps.
 *
 * <p><strong>Core responsibilities:</strong></p>
 * <ul>
 *     <li>Storing and retrieving values based on a key</li>
 *     <li>Supporting optional time-to-live (TTL) values for cached entries</li>
 *     <li>Providing asynchronous APIs for non-blocking cache operations</li>
 *     <li>Evicting individual keys or multiple keys using a pattern</li>
 * </ul>
 *
 * <p><strong>Typical usage scenarios:</strong></p>
 * <ul>
 *     <li>Application-level caching of frequently accessed data</li>
 *     <li>Result caching for expensive method calls (e.g., database or API calls)</li>
 *     <li>Eviction of stale data based on events or updates</li>
 * </ul>
 *
 * @author Evaristus Adimonyemma
 */
public interface ResourceManagementCacheProvider {
    /**
     * Stores a value in the cache with the given key and TTL.
     *
     * @param key   the cache key
     * @param value the value to cache
     * @param ttl   the time-to-live duration for the cache entry
     * @param <T>   the type of the value
     */
    <T> void put(String key, T value, Duration ttl);

    /**
     * Retrieves a value from the cache by key.
     *
     * @param key  the cache key
     * @param type the expected class of the cached value
     * @param <T>  the type of the value
     * @return an Optional containing the value if present, or empty if not found
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Asynchronously retrieves a value from the cache by key.
     *
     * @param key  the cache key
     * @param type the expected class of the cached value
     * @param <T>  the type of the value
     * @return a CompletableFuture containing an Optional of the value
     */
    <T> CompletableFuture<Optional<T>> getAsync(String key, Class<T> type);

    /**
     * Asynchronously stores a value in the cache with the given key and TTL.
     *
     * @param key   the cache key
     * @param value the value to cache
     * @param ttl   the time-to-live duration for the cache entry
     * @param <T>   the type of the value
     */
    <T> void putAsync(String key, T value, Duration ttl);

    /**
     * Removes a cache entry by key.
     *
     * @param key the cache key
     */
    void evict(String key);

    /**
     * Removes all cache entries.
     */
    void evictAll();

    /**
     * Removes cache entries that match a specific key pattern.
     *
     * @param pattern the pattern to match keys
     */
    void evictByPattern(String pattern);

    /**
     * Checks whether a key exists in the cache.
     *
     * @param key the cache key
     * @return true if the key exists, false otherwise
     */
    boolean exists(String key);

    /**
     * Returns the total number of entries in the cache.
     *
     * @return the cache size
     */
    long size();

    /**
     * Clears all entries from the cache.
     */
    void clear();
}