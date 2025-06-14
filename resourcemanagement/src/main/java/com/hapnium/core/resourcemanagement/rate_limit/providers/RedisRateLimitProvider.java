package com.hapnium.core.resourcemanagement.rate_limit.providers;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitRequest;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;

/**
 * Redis-based rate limit provider supporting distributed environments.
 * <p>
 * Uses Lua scripts for atomic operations, making it ideal for scalable rate limiting.
 * </p>
 *
 * @see AbstractResourceManagementRateLimitProvider
 * @see com.hapnium.core.resourcemanagement.rate_limit.ResourceManagementRateLimitProvider
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
public class RedisRateLimitProvider extends AbstractResourceManagementRateLimitProvider {
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Lua script for sliding window rate limiting.
     */
    private static final String SLIDING_WINDOW_SCRIPT = """
        local key = KEYS[1]
        local window = tonumber(ARGV[1])
        local limit = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])

        -- Remove expired entries
        redis.call('ZREMRANGEBYSCORE', key, 0, now - window * 1000)

        -- Count current requests
        local current = redis.call('ZCARD', key)

        if current < limit then
            -- Add current request
            redis.call('ZADD', key, now, now)
            redis.call('EXPIRE', key, window)
            return {1, limit - current - 1, current + 1}
        else
            return {0, 0, current}
        end
    """;

    private final DefaultRedisScript<Object> slidingWindowScript;

    /**
     * Initializes the Redis rate limiter and loads the Lua script.
     *
     * @param redisTemplate Redis access object
     * @param properties    configuration for rate limits
     */
    public RedisRateLimitProvider(RedisTemplate<String, Object> redisTemplate, ResourceManagementProperty properties) {
        super(properties, "[REDIS_RATE_LIMIT PROVIDER]:");
        this.redisTemplate = redisTemplate;
        this.slidingWindowScript = new DefaultRedisScript<>(SLIDING_WINDOW_SCRIPT, Object.class);
        log.info("{} Redis rate limit provider initialized", LOG_NAME);
    }

    @Override
    protected RateLimitResult checkSlidingWindow(String key, RateLimitRequest request) {
        long now = Instant.now().toEpochMilli();
        long windowMs = request.getWindow().toMillis();

        Object result = redisTemplate.execute(slidingWindowScript,
                Collections.singletonList(key),
                windowMs, request.getLimit(), now);

        if (result instanceof java.util.List<?> list && list.size() == 3) {
            boolean allowed = ((Number) list.get(0)).intValue() == 1;
            long remaining = ((Number) list.get(1)).longValue();
            long total = ((Number) list.get(2)).longValue();

            Duration timeUntilReset = Duration.ofMillis(windowMs);

            return RateLimitResult.builder()
                    .allowed(allowed)
                    .remainingRequests(remaining)
                    .totalRequests(total)
                    .timeUntilReset(timeUntilReset)
                    .resetTime(Instant.now().plus(timeUntilReset))
                    .strategy("sliding-window")
                    .limit(request.getLimit())
                    .window(request.getWindow())
                    .build();
        }

        return RateLimitResult.denied(0, request.getLimit(), request.getWindow());
    }

    @Override
    protected RateLimitResult checkTokenBucket(String key, RateLimitRequest request) {
        // Simple token bucket implementation using Redis
        String tokensKey = key + ":tokens";
        String lastRefillKey = key + ":lastRefill";

        long now = Instant.now().toEpochMilli();
        Long lastRefill = (Long) redisTemplate.opsForValue().get(lastRefillKey);

        if (lastRefill == null) {
            lastRefill = now;
            redisTemplate.opsForValue().set(lastRefillKey, lastRefill, request.getWindow());
            redisTemplate.opsForValue().set(tokensKey, request.getLimit(), request.getWindow());
        }

        // Calculate tokens to add
        long timePassed = now - lastRefill;
        long tokensToAdd = (timePassed * request.getLimit()) / request.getWindow().toMillis();

        Long currentTokens = (Long) redisTemplate.opsForValue().get(tokensKey);
        if (currentTokens == null) {
            currentTokens = (long) request.getLimit();
        }

        currentTokens = Math.min(request.getLimit(), currentTokens + tokensToAdd);

        if (currentTokens > 0) {
            redisTemplate.opsForValue().set(tokensKey, currentTokens - 1, request.getWindow());
            redisTemplate.opsForValue().set(lastRefillKey, now, request.getWindow());

            return RateLimitResult.allowed(currentTokens - 1, request.getLimit() - currentTokens + 1,
                    Duration.ofMillis((request.getWindow().toMillis() * (request.getLimit() - currentTokens + 1)) / request.getLimit()));
        } else {
            return RateLimitResult.denied(0, request.getLimit(),
                    Duration.ofMillis(request.getWindow().toMillis() / request.getLimit()));
        }
    }

    @Override
    protected RateLimitResult checkFixedWindow(String key, RateLimitRequest request) {
        long windowStart = (Instant.now().toEpochMilli() / request.getWindow().toMillis()) * request.getWindow().toMillis();
        String windowKey = key + ":" + windowStart;

        Long count = redisTemplate.opsForValue().increment(windowKey);
        count = count != null ? count : 0L;

        if (count == 1) {
            redisTemplate.expire(windowKey, request.getWindow());
        }

        boolean allowed = count <= request.getLimit();
        long remaining = Math.max(0, request.getLimit() - count);

        long nextWindowStart = windowStart + request.getWindow().toMillis();
        Duration timeUntilReset = Duration.ofMillis(nextWindowStart - Instant.now().toEpochMilli());

        return RateLimitResult.builder()
                .allowed(allowed)
                .remainingRequests(remaining)
                .totalRequests(count)
                .timeUntilReset(timeUntilReset)
                .resetTime(Instant.ofEpochMilli(nextWindowStart))
                .strategy("fixed-window")
                .limit(request.getLimit())
                .window(request.getWindow())
                .build();
    }

    @Override
    public void resetRateLimit(String key) {
        try {
            String fullKey = buildKey(key);
            redisTemplate.delete(fullKey);
            redisTemplate.delete(fullKey + ":tokens");
            redisTemplate.delete(fullKey + ":lastRefill");
        } catch (Exception e) {
            log.error("Error resetting rate limit for key: {}", key, e);
        }
    }

    @Override
    public void clearAll() {
        try {
            Set<String> keys = redisTemplate.keys(buildKey("*"));
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("Error clearing all rate limits", e);
        }
    }

    @Override
    public Long getRequestCount(String key) {
        try {
            String fullKey = buildKey(key);
            return redisTemplate.opsForZSet().count(fullKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        } catch (Exception e) {
            log.error("Error getting request count for key: {}", key, e);
            return 0L;
        }
    }

    @Override
    protected String buildKey(String key) {
        return property().getRedis().getKeyPrefix() + key;
    }
}