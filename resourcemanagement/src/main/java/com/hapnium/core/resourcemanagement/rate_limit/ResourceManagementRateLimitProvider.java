package com.hapnium.core.resourcemanagement.rate_limit;

import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitRequest;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import com.hapnium.core.resourcemanagement.rate_limit.providers.MemoryRateLimitProvider;
import com.hapnium.core.resourcemanagement.rate_limit.providers.RedisRateLimitProvider;
import com.hapnium.core.resourcemanagement.rate_limit.providers.SimpleRateLimitProvider;

/**
 * Defines the contract for rate limiting provider implementations.
 * <p>
 * Implementations of this interface are responsible for applying rate limiting logic such as token buckets,
 * fixed or sliding windows, and request counters. Each implementation may rely on different backends
 * (e.g., in-memory, Redis) but must conform to a consistent API.
 * </p>
 *
 * <p>Main Responsibilities:</p>
 * <ul>
 *   <li>Evaluate whether a request exceeds the defined rate limit</li>
 *   <li>Track and return request statistics per key</li>
 *   <li>Support resetting and clearing of usage data</li>
 * </ul>
 *
 * <p>
 * Providers should be registered via Spring Boot configuration and are selected based on application properties.
 * </p>
 *
 * @see RateLimitRequest
 * @see RateLimitResult
 * @see RedisRateLimitProvider
 * @see MemoryRateLimitProvider
 * @see SimpleRateLimitProvider
 */
public interface ResourceManagementRateLimitProvider {
    /**
     * Checks if the request exceeds the defined rate limit.
     *
     * @param request the request details including key, limit, and window duration
     * @return result of the rate limit evaluation
     */
    RateLimitResult checkRateLimit(RateLimitRequest request);

    /**
     * Resets rate limit tracking data for a specific key.
     *
     * @param key the unique identifier (e.g., user ID or IP) whose limit data should be cleared
     */
    void resetRateLimit(String key);

    /**
     * Clears all in-memory or distributed rate limiting state.
     */
    void clearAll();

    /**
     * Returns the number of requests seen for a specific key.
     *
     * @param key the key to look up
     * @return the number of requests, or {@code null} if not tracked
     */
    Long getRequestCount(String key);
}