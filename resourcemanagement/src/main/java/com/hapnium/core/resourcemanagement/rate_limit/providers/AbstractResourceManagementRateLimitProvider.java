package com.hapnium.core.resourcemanagement.rate_limit.providers;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.rate_limit.ResourceManagementRateLimitProvider;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitRequest;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Abstract base class for rate limit providers, implementing shared logic for various
 * rate limit strategies and configurations.
 * <p>
 * Concrete implementations must provide the actual rate limiting logic,
 * which can be memory-based, Redis-backed, or others.
 * </p>
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
abstract class AbstractResourceManagementRateLimitProvider implements ResourceManagementRateLimitProvider {
    /** Global application property manager */
    protected final ResourceManagementProperty properties;

    /** Log prefix for the implementing provider */
    protected final String LOG_NAME;

    /**
     * Constructs the base rate limit provider.
     *
     * @param properties the global property manager
     * @param logName    the log prefix name for the subclass
     */
    protected AbstractResourceManagementRateLimitProvider(ResourceManagementProperty properties, String logName) {
        this.properties = properties;
        this.LOG_NAME = logName;
    }

    /**
     * Returns the configured rate limit properties from the global property manager.
     *
     * @return rate limit configuration properties
     */
    protected ResourceManagementProperty.RateLimitProperties property() {
        return properties.getRateLimit();
    }

    /**
     * Entry point for checking a rate limit, delegating to the appropriate strategy.
     * Handles fallback and error conditions as well.
     *
     * @param request the rate limit request
     * @return the result of the rate limit evaluation
     */
    @Override
    public RateLimitResult checkRateLimit(RateLimitRequest request) {
        try {
            String strategy = request.getStrategy() != null ? request.getStrategy() : "sliding-window";
            String fullKey = buildKey(request.getKey());

            return switch (strategy.toLowerCase()) {
                case "sliding-window" -> checkSlidingWindow(fullKey, request);
                case "token-bucket" -> checkTokenBucket(fullKey, request);
                case "fixed-window" -> checkFixedWindow(fullKey, request);
                default -> {
                    log.warn("{} Unknown strategy: {}, using sliding-window", LOG_NAME, strategy);
                    yield checkSlidingWindow(fullKey, request);
                }
            };
        } catch (Exception e) {
            log.error("{} Error checking rate limit for key: {}", LOG_NAME, request.getKey(), e);
            if (property().isSkipOnFailure()) {
                return RateLimitResult.allowed(request.getLimit(), 0, Duration.ZERO);
            } else {
                return RateLimitResult.denied(0, request.getLimit(), request.getWindow());
            }
        }
    }

    /**
     * Executes the sliding window rate limiting strategy.
     *
     * @param key     the full namespaced key
     * @param request the rate limit request
     * @return the result of the rate limit evaluation
     */
    protected abstract RateLimitResult checkSlidingWindow(String key, RateLimitRequest request);

    /**
     * Executes the token bucket rate limiting strategy.
     *
     * @param key     the full namespaced key
     * @param request the rate limit request
     * @return the result of the rate limit evaluation
     */
    protected abstract RateLimitResult checkTokenBucket(String key, RateLimitRequest request);

    /**
     * Executes the fixed window rate limiting strategy.
     *
     * @param key     the full namespaced key
     * @param request the rate limit request
     * @return the result of the rate limit evaluation
     */
    protected abstract RateLimitResult checkFixedWindow(String key, RateLimitRequest request);

    /**
     * Builds the full Redis/memory key with appropriate prefixing.
     *
     * @param key the base key
     * @return the fully qualified key
     */
    protected abstract String buildKey(String key);
}