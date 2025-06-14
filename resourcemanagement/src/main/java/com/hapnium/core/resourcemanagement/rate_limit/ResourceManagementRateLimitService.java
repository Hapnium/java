package com.hapnium.core.resourcemanagement.rate_limit;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitRequest;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service layer for handling request rate limiting using a configurable {@link ResourceManagementRateLimitProvider}.
 * <p>
 * This service encapsulates the logic for enforcing rate limits across various strategies (e.g., Redis, in-memory),
 * coordinating configuration parameters, key generation, and exception handling.
 * </p>
 *
 * <p>Main Features:</p>
 * <ul>
 *   <li>Delegates rate limiting checks to the selected provider implementation</li>
 *   <li>Manages rate limit keys and user/IP awareness</li>
 *   <li>Handles exception wrapping and logging for limit violations</li>
 *   <li>Supports reset and introspection of current usage metrics</li>
 * </ul>
 *
 * <p>
 * This service is typically invoked by an AOP aspect to automatically intercept and rate-limit annotated methods.
 * It can also be called manually where explicit control is required.
 * </p>
 *
 * <p>
 * The provider used is selected and configured through the {@code hapnium.resourcemanagement.rate-limit.*} properties.
 * </p>
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceManagementRateLimitService {
    private final ResourceManagementRateLimitProvider rateLimitProvider;
    private final ResourceManagementProperty properties;

    private final String LOG_NAME = "[RATE LIMIT SERVICE]:";

    /**
     * Gets the current rate limiting configuration properties.
     *
     * @return the rate limit properties
     */
    private ResourceManagementProperty.RateLimitProperties property() {
        return properties.getRateLimit();
    }

    /**
     * Checks whether a given request exceeds rate limits based on its configuration.
     *
     * @param request the rate limit request
     * @return result indicating if the request is allowed or denied
     */
    public RateLimitResult checkRateLimit(RateLimitRequest request) {
        if (!property().isEnabled()) {
            log.debug("{} Rate limiting is disabled, allowing request for key: {}", LOG_NAME, request.getKey());
            return RateLimitResult.allowed(request.getLimit(), 0, Duration.ZERO);
        }

        try {
            RateLimitResult result = rateLimitProvider.checkRateLimit(request);
            log.debug("{} Rate limit check for key: {} - allowed: {}, remaining: {}", LOG_NAME, request.getKey(), result.isAllowed(), result.getRemainingRequests());
            return result;
        } catch (Exception e) {
            log.error("{} Failed to check rate limit for key: {}", LOG_NAME, request.getKey(), e);

            if (property().isSkipOnFailure()) {
                log.debug("{} Skipping rate limit check due to failure for key: {}", LOG_NAME, request.getKey());
                return RateLimitResult.allowed(request.getLimit(), 0, Duration.ZERO);
            } else {
                return RateLimitResult.denied(0, request.getLimit(), request.getWindow());
            }
        }
    }

    /**
     * Convenience method to check if a request is allowed with the default limit and window.
     *
     * @param key the request key
     * @return true if allowed, false otherwise
     */
    public boolean isAllowed(String key) {
        return isAllowed(key, property().getDefaultLimit(), property().getDefaultWindow());
    }

    /**
     * Checks if a request is allowed using the provided limit and window.
     *
     * @param key    the request key
     * @param limit  max allowed requests
     * @param window time window for the limit
     * @return true if allowed, false otherwise
     */
    public boolean isAllowed(String key, int limit, Duration window) {
        return isAllowed(key, limit, window, property().getDefaultStrategy());
    }

    /**
     * Checks if a request is allowed using the provided limit, window, and strategy.
     *
     * @param key      the request key
     * @param limit    max allowed requests
     * @param window   time window for the limit
     * @param strategy rate limit strategy (e.g. sliding-window, token-bucket)
     * @return true if allowed, false otherwise
     */
    public boolean isAllowed(String key, int limit, Duration window, String strategy) {
        RateLimitRequest request = RateLimitRequest.builder()
                .key(key)
                .limit(limit)
                .window(window)
                .strategy(strategy)
                .build();

        RateLimitResult result = checkRateLimit(request);
        return result.isAllowed();
    }

    /**
     * Resets the rate limit tracking for a specific key.
     *
     * @param key the request key to reset
     */
    public void resetRateLimit(String key) {
        if (!property().isEnabled()) {
            return;
        }

        try {
            rateLimitProvider.resetRateLimit(key);
            log.debug("{} Reset rate limit for key: {}", LOG_NAME, key);
        } catch (Exception e) {
            log.error("{} Failed to reset rate limit for key: {}", LOG_NAME, key, e);
        }
    }

    /**
     * Clears all rate limiting data.
     */
    public void clearAll() {
        if (!property().isEnabled()) {
            return;
        }

        try {
            rateLimitProvider.clearAll();
            log.info("{} Cleared all rate limits", LOG_NAME);
        } catch (Exception e) {
            log.error("{} Failed to clear all rate limits", LOG_NAME, e);
        }
    }

    /**
     * Retrieves the current number of recorded requests for a given key.
     *
     * @param key the request key
     * @return the number of recorded requests
     */
    public Long getRequestCount(String key) {
        if (!property().isEnabled()) {
            return 0L;
        }

        try {
            return rateLimitProvider.getRequestCount(key);
        } catch (Exception e) {
            log.error("{} Failed to get request count for key: {}", LOG_NAME, key, e);
            return 0L;
        }
    }

    /**
     * Gets the number of remaining allowed requests for a given key.
     *
     * @param key the request key
     * @return number of remaining requests
     */
    public long getRemainingRequests(String key) {
        RateLimitRequest request = RateLimitRequest.of(key, property().getDefaultLimit(), property().getDefaultWindow());
        RateLimitResult result = checkRateLimit(request);
        return result.getRemainingRequests();
    }

    /**
     * Gets the time duration until the current rate limit window resets.
     *
     * @param key the request key
     * @return time until the rate limit resets
     */
    public Duration getTimeUntilReset(String key) {
        RateLimitRequest request = RateLimitRequest.of(key, property().getDefaultLimit(), property().getDefaultWindow());
        RateLimitResult result = checkRateLimit(request);
        return result.getTimeUntilReset();
    }
}