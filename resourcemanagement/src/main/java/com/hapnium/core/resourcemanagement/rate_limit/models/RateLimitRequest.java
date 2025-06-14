package com.hapnium.core.resourcemanagement.rate_limit.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * Represents a request to check or enforce a rate limit policy.
 */
@Getter
@Setter
@Builder
public class RateLimitRequest {
    /**
     * The key used to identify the rate limit bucket.
     */
    private String key;

    /**
     * The maximum number of allowed requests in the given time window.
     */
    private int limit;

    /**
     * The duration of the rate limit window.
     */
    private Duration window;

    /**
     * The strategy used for rate limiting, e.g., "sliding-window", "fixed-window", or "token-bucket".
     */
    private String strategy;

    /**
     * The user type (e.g., "admin", "guest") if applicable.
     */
    private String userType;

    /**
     * The endpoint associated with this rate limit.
     */
    private String endpoint;

    /**
     * Factory method to create a request with the default "sliding-window" strategy.
     */
    public static RateLimitRequest of(String key, int limit, Duration window) {
        return RateLimitRequest.builder()
                .key(key)
                .limit(limit)
                .window(window)
                .strategy("sliding-window")
                .build();
    }

    /**
     * Factory method to create a request with a specific rate limiting strategy.
     */
    public static RateLimitRequest of(String key, int limit, Duration window, String strategy) {
        return RateLimitRequest.builder()
                .key(key)
                .limit(limit)
                .window(window)
                .strategy(strategy)
                .build();
    }
}