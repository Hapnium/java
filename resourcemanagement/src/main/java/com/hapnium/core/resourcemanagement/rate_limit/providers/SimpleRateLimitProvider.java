package com.hapnium.core.resourcemanagement.rate_limit.providers;

import com.hapnium.core.resourcemanagement.rate_limit.ResourceManagementRateLimitProvider;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitRequest;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A naive, non-expiring in-memory rate limiter.
 * <p>
 * This fallback implementation is ideal for testing or low-complexity use cases.
 * It does not support TTL or expiration.
 * </p>
 *
 * @see AbstractResourceManagementRateLimitProvider
 * @see com.hapnium.core.resourcemanagement.rate_limit.ResourceManagementRateLimitProvider
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
public class SimpleRateLimitProvider implements ResourceManagementRateLimitProvider {
    private final ConcurrentMap<String, RateLimitEntry> rateLimits = new ConcurrentHashMap<>();
    private final String keyPrefix;

    private final String LOG_NAME = "[SIMPLE_RATE_LIMIT PROVIDER]";

    /**
     * Constructs a simple rate limiter.
     *
     * @param keyPrefix optional prefix for all rate limit keys
     */
    public SimpleRateLimitProvider(String keyPrefix) {
        this.keyPrefix = keyPrefix != null ? keyPrefix : "rl:";
        log.info("{} Simple in-memory rate limit provider initialized", LOG_NAME);
    }

    @Override
    public RateLimitResult checkRateLimit(RateLimitRequest request) {
        String fullKey = keyPrefix + request.getKey();
        Instant now = Instant.now();
        
        RateLimitEntry entry = rateLimits.computeIfAbsent(fullKey, k -> new RateLimitEntry());
        
        synchronized (entry) {
            // Simple sliding window implementation
            if (entry.windowStart == null || Duration.between(entry.windowStart, now).compareTo(request.getWindow()) >= 0) {
                // Reset window
                entry.windowStart = now;
                entry.count.set(1);
                
                return RateLimitResult.builder()
                        .allowed(true)
                        .remainingRequests(request.getLimit() - 1)
                        .totalRequests(1)
                        .timeUntilReset(request.getWindow())
                        .resetTime(now.plus(request.getWindow()))
                        .strategy(request.getStrategy())
                        .limit(request.getLimit())
                        .window(request.getWindow())
                        .build();
            } else {
                // Within the window
                long currentCount = entry.count.incrementAndGet();
                boolean allowed = currentCount <= request.getLimit();
                
                if (!allowed) {
                    entry.count.decrementAndGet(); // Rollback if is not allowed
                }
                
                long remaining = Math.max(0, request.getLimit() - currentCount);
                Duration timeUntilReset = request.getWindow().minus(Duration.between(entry.windowStart, now));
                
                return RateLimitResult.builder()
                        .allowed(allowed)
                        .remainingRequests(remaining)
                        .totalRequests(currentCount)
                        .timeUntilReset(timeUntilReset)
                        .resetTime(entry.windowStart.plus(request.getWindow()))
                        .strategy(request.getStrategy())
                        .limit(request.getLimit())
                        .window(request.getWindow())
                        .build();
            }
        }
    }

    @Override
    public void resetRateLimit(String key) {
        String fullKey = keyPrefix + key;

        log.info("{} Simple in-memory rate limit provider reset for {}", LOG_NAME, fullKey);
        rateLimits.remove(fullKey);
    }

    @Override
    public void clearAll() {
        rateLimits.clear();
        log.info("{} Simple in-memory rate limit provider cleared", LOG_NAME);
    }

    @Override
    public Long getRequestCount(String key) {
        String fullKey = keyPrefix + key;
        RateLimitEntry entry = rateLimits.get(fullKey);
        return entry != null ? entry.count.get() : 0L;
    }

    private static class RateLimitEntry {
        volatile Instant windowStart;
        final AtomicLong count = new AtomicLong(0);
    }
}