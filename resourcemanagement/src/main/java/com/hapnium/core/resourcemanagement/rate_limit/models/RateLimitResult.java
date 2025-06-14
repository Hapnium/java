package com.hapnium.core.resourcemanagement.rate_limit.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents the result of a rate limit check.
 */
@Getter
@Setter
@Builder
public class RateLimitResult {
    /**
     * Whether the request is allowed under the current rate limit.
     */
    private boolean allowed;

    /**
     * Number of remaining requests available in the current window.
     */
    private long remainingRequests;

    /**
     * Total number of requests made in the current window.
     */
    private long totalRequests;

    /**
     * Duration until the rate limit window resets.
     */
    private Duration timeUntilReset;

    /**
     * The exact timestamp when the rate limit window resets.
     */
    private Instant resetTime;

    /**
     * The rate limiting strategy applied (e.g., "sliding-window").
     */
    private String strategy;

    /**
     * The configured request limit for this window.
     */
    private int limit;

    /**
     * The duration of the rate limit window.
     */
    private Duration window;

    /**
     * Static factory method to construct an allowed result.
     */
    public static RateLimitResult allowed(long remainingRequests, long totalRequests, Duration timeUntilReset) {
        return RateLimitResult.builder()
                .allowed(true)
                .remainingRequests(remainingRequests)
                .totalRequests(totalRequests)
                .timeUntilReset(timeUntilReset)
                .build();
    }

    /**
     * Static factory method to construct a denied result.
     */
    public static RateLimitResult denied(long remainingRequests, long totalRequests, Duration timeUntilReset) {
        return RateLimitResult.builder()
                .allowed(false)
                .remainingRequests(remainingRequests)
                .totalRequests(totalRequests)
                .timeUntilReset(timeUntilReset)
                .build();
    }
}