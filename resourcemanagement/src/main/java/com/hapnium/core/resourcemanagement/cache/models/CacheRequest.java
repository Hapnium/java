package com.hapnium.core.resourcemanagement.cache.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * Represents a request to store a value in the cache.
 *
 * @param <T> The type of the value being cached.
 */
@Getter
@Setter
@Builder
public class CacheRequest<T> {
    /**
     * The unique key used to identify the cached entry.
     */
    private String key;

    /**
     * The value to be cached.
     */
    private T value;

    /**
     * The duration for which the cached value remains valid.
     */
    private Duration ttl;

    /**
     * The name of the cache store (e.g., Redis, Caffeine) where the entry will be saved.
     */
    private String cacheName;

    /**
     * Whether the caching operation should be performed asynchronously.
     */
    private boolean async;

    /**
     * Whether null values should be stored in the cache.
     */
    private boolean cacheNullValues;
}