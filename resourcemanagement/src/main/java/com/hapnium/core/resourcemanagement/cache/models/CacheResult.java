package com.hapnium.core.resourcemanagement.cache.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents the result of a cache lookup.
 *
 * @param <T> The type of the cached value.
 */
@Getter
@Setter
@Builder
public class CacheResult<T> {
    /**
     * The value retrieved from the cache, or null if not found.
     */
    private T value;

    /**
     * Whether the cache lookup resulted in a hit.
     */
    private boolean hit;

    /**
     * The key used to query the cache.
     */
    private String key;

    /**
     * The timestamp at which the value was cached.
     */
    private Instant createdAt;

    /**
     * The expiration timestamp of the cached value.
     */
    private Instant expiresAt;

    /**
     * The source of the cached data (e.g., "redis", "caffeine").
     */
    private String source;
}