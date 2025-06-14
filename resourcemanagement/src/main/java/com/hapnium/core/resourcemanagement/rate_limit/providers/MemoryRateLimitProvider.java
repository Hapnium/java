package com.hapnium.core.resourcemanagement.rate_limit.providers;

import com.hapnium.core.resourcemanagement.ResourceManagementProperty;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitRequest;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory rate limit provider using a thread-safe map and scheduled cleanup.
 * <p>
 * Implements a sliding window and other strategies based on configuration.
 * Suitable for local development, testing, and single-node deployments.
 * </p>
 *
 * @see AbstractResourceManagementRateLimitProvider
 * @see com.hapnium.core.resourcemanagement.rate_limit.ResourceManagementRateLimitProvider
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
public class MemoryRateLimitProvider extends AbstractResourceManagementRateLimitProvider {
    private final ConcurrentMap<String, RateLimitEntry> rateLimits = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Initializes the in-memory provider and schedules cleanup.
     *
     * @param properties the application configuration
     */
    public MemoryRateLimitProvider(ResourceManagementProperty properties) {
        super(properties, "[MEMORY_RATE_LIMIT PROVIDER]:");
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "rate-limit-cleanup");
            t.setDaemon(true);
            return t;
        });

        // Schedule a cleanup task
        this.cleanupExecutor.scheduleAtFixedRate(
                this::cleanup,
                property().getMemory().getCleanupInterval().toMinutes(),
                property().getMemory().getCleanupInterval().toMinutes(),
                TimeUnit.MINUTES
        );

        log.info("[MEMORY_RATE_LIMIT PROVIDER]: Memory rate limit provider initialized");
    }

    @Override
    protected RateLimitResult checkSlidingWindow(String key, RateLimitRequest request) {
        lock.lock();
        try {
            RateLimitEntry entry = rateLimits.computeIfAbsent(key, k -> new RateLimitEntry());
            Instant now = Instant.now();

            // Remove expired requests
            entry.requests.removeIf(timestamp -> Duration.between(timestamp, now).compareTo(request.getWindow()) > 0);

            boolean allowed = entry.requests.size() < request.getLimit();

            if (allowed) {
                entry.requests.add(now);
            }

            long remaining = Math.max(0, request.getLimit() - entry.requests.size());
            Duration timeUntilReset = entry.requests.isEmpty() ? Duration.ZERO :
                    request.getWindow().minus(Duration.between(entry.requests.getFirst(), now));

            return RateLimitResult.builder()
                    .allowed(allowed)
                    .remainingRequests(remaining)
                    .totalRequests(entry.requests.size())
                    .timeUntilReset(timeUntilReset)
                    .resetTime(now.plus(timeUntilReset))
                    .strategy("sliding-window")
                    .limit(request.getLimit())
                    .window(request.getWindow())
                    .build();
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected RateLimitResult checkTokenBucket(String key, RateLimitRequest request) {
        lock.lock();
        try {
            RateLimitEntry entry = rateLimits.computeIfAbsent(key, k -> new RateLimitEntry());
            Instant now = Instant.now();

            if (entry.lastRefill == null) {
                entry.lastRefill = now;
                entry.tokens.set(request.getLimit());
            }

            // Calculate tokens to add
            long timePassed = Duration.between(entry.lastRefill, now).toMillis();
            long tokensToAdd = (timePassed * request.getLimit()) / request.getWindow().toMillis();

            if (tokensToAdd > 0) {
                entry.tokens.set(Math.min(request.getLimit(), entry.tokens.get() + tokensToAdd));
                entry.lastRefill = now;
            }

            boolean allowed = entry.tokens.get() > 0;

            if (allowed) {
                entry.tokens.decrementAndGet();
            }

            long remaining = entry.tokens.get();
            Duration timeUntilReset = Duration.ofMillis(
                    (request.getWindow().toMillis() * (request.getLimit() - remaining)) / request.getLimit());

            return RateLimitResult.builder()
                    .allowed(allowed)
                    .remainingRequests(remaining)
                    .totalRequests(request.getLimit() - remaining)
                    .timeUntilReset(timeUntilReset)
                    .resetTime(now.plus(timeUntilReset))
                    .strategy("token-bucket")
                    .limit(request.getLimit())
                    .window(request.getWindow())
                    .build();
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected RateLimitResult checkFixedWindow(String key, RateLimitRequest request) {
        lock.lock();
        try {
            RateLimitEntry entry = rateLimits.computeIfAbsent(key, k -> new RateLimitEntry());
            Instant now = Instant.now();

            long windowStart = (now.toEpochMilli() / request.getWindow().toMillis()) * request.getWindow().toMillis();

            if (entry.windowStart == null || entry.windowStart.toEpochMilli() != windowStart) {
                entry.windowStart = Instant.ofEpochMilli(windowStart);
                entry.count.set(0);
            }

            long currentCount = entry.count.incrementAndGet();
            boolean allowed = currentCount <= request.getLimit();

            if (!allowed) {
                entry.count.decrementAndGet(); // Rollback if is not allowed
            }

            long remaining = Math.max(0, request.getLimit() - currentCount);
            long nextWindowStart = windowStart + request.getWindow().toMillis();
            Duration timeUntilReset = Duration.ofMillis(nextWindowStart - now.toEpochMilli());

            return RateLimitResult.builder()
                    .allowed(allowed)
                    .remainingRequests(remaining)
                    .totalRequests(currentCount)
                    .timeUntilReset(timeUntilReset)
                    .resetTime(Instant.ofEpochMilli(nextWindowStart))
                    .strategy("fixed-window")
                    .limit(request.getLimit())
                    .window(request.getWindow())
                    .build();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void resetRateLimit(String key) {
        String fullKey = buildKey(key);
        rateLimits.remove(fullKey);
    }

    @Override
    public void clearAll() {
        rateLimits.clear();
    }

    @Override
    public Long getRequestCount(String key) {
        String fullKey = buildKey(key);
        RateLimitEntry entry = rateLimits.get(fullKey);
        return entry != null ? (long) entry.requests.size() : 0L;
    }

    private void cleanup() {
        lock.lock();
        try {
            Instant cutoff = Instant.now().minus(Duration.ofHours(1)); // Remove entries older than 1 hour
            rateLimits.entrySet().removeIf(entry -> {
                RateLimitEntry rateLimitEntry = entry.getValue();
                return rateLimitEntry.lastRefill != null && rateLimitEntry.lastRefill.isBefore(cutoff);
            });
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected String buildKey(String key) {
        return property().getKeyPrefix() + key;
    }

    private static class RateLimitEntry {
        final java.util.List<Instant> requests = new CopyOnWriteArrayList<>();
        final AtomicLong tokens = new AtomicLong(0);
        final AtomicLong count = new AtomicLong(0);
        volatile Instant lastRefill;
        volatile Instant windowStart;
    }
}