package com.hapnium.core.resourcemanagement.cache;

import com.hapnium.core.resourcemanagement.cache.annotations.CacheEvict;
import com.hapnium.core.resourcemanagement.cache.annotations.CachePut;
import com.hapnium.core.resourcemanagement.cache.annotations.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Aspect for handling caching behavior via method-level interception.
 * <p>
 * This aspect processes custom caching annotations (e.g., {@code @Cached}, {@code @CacheEvict})
 * and delegates the actual cache operations to the {@link ResourceManagementCacheService}.
 * It supports both cache retrieval and eviction, as well as conditional and key-based logic.
 * </p>
 *
 * <p>Main Responsibilities:</p>
 * <ul>
 *   <li>Intercept methods annotated for caching or eviction</li>
 *   <li>Resolve cache keys using {@link ResourceManagementCacheKeyGenerator}</li>
 *   <li>Perform cache get/put operations transparently</li>
 *   <li>Handle TTL and key pattern-based eviction</li>
 *   <li>Ensure this executing after rate limiting (ordered with {@code @Order(2)})</li>
 * </ul>
 *
 * <p>
 * This aspect relies on a properly configured {@link ResourceManagementCacheProvider} and integrates
 * seamlessly with both synchronous and asynchronous method signatures.
 * </p>
 *
 * <p>
 * Caching behavior can be fine-tuned via application properties or by configuring custom cache providers.
 * </p>
 *
 * @see com.hapnium.core.resourcemanagement.cache.annotations.Cacheable
 * @see com.hapnium.core.resourcemanagement.cache.annotations.CacheEvict
 * @see ResourceManagementCacheService
 * @see ResourceManagementCacheKeyGenerator
 * @see ResourceManagementCacheProvider
 */
@Slf4j
@Aspect
@Order(2) // Execute after rate limiting
@Component
@RequiredArgsConstructor
class ResourceManagementCacheAspect {
    private final ResourceManagementCacheService resourceManagementCacheService;
    private final ResourceManagementCacheKeyGenerator keyGenerator;

    private final String LOG_NAME = "[CACHE ASPECT]:";

    @Around("@annotation(cacheable)")
    public Object cacheable(ProceedingJoinPoint joinPoint, @NonNull Cacheable cacheable) throws Throwable {
        String key = keyGenerator.generateKey(
                joinPoint,
                cacheable.key(),
                cacheable.keyPrefix(),
                cacheable.includeMethodName(),
                cacheable.includeClassName(),
                cacheable.includeParameters()
        );
        
        Class<?> returnType = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getReturnType();
        
        // Try to get from cache first
        Optional<?> cachedValue = resourceManagementCacheService.get(key, returnType);
        if (cachedValue.isPresent()) {
            log.debug("{} Cache hit for key: {}", LOG_NAME, key);
            return cachedValue.get();
        }
        
        log.debug("{} Cache miss for key: {}", LOG_NAME, key);
        
        // Execute the method
        Object result = joinPoint.proceed();
        
        // Cache the result if not null (unless cacheNull is true)
        if (result != null || cacheable.cacheNull()) {
            Duration ttl = cacheable.ttl() > 0 ? Duration.of(cacheable.ttl(), cacheable.ttlUnit()) : null; // Use default TTL
            
            resourceManagementCacheService.put(key, result, ttl);
            log.debug("{} Cached result for key: {}", LOG_NAME, key);
        }
        
        return result;
    }

    @Around("@annotation(cachePut)")
    public Object cachePut(ProceedingJoinPoint joinPoint, @NonNull CachePut cachePut) throws Throwable {
        String key = keyGenerator.generateKey(joinPoint, cachePut.key(), cachePut.keyPrefix(), 
                cachePut.includeMethodName(), cachePut.includeClassName(), cachePut.includeParameters());
        
        // Always execute the method
        Object result = joinPoint.proceed();
        
        // Always cache the result (unless a result is null and cacheNull is false)
        if (result != null || cachePut.cacheNull()) {
            Duration ttl = cachePut.ttl() > 0 ? Duration.of(cachePut.ttl(), cachePut.ttlUnit()) : null; // Use default TTL
            
            resourceManagementCacheService.put(key, result, ttl);
            log.debug("{} Updated cache for key: {}", LOG_NAME, key);
        }
        
        return result;
    }

    @Around("@annotation(cacheEvict)")
    public Object cacheEvict(ProceedingJoinPoint joinPoint, @NonNull CacheEvict cacheEvict) throws Throwable {
        // Execute the method first (unless beforeInvocation is true)
        Object result;
        
        if (cacheEvict.beforeInvocation()) {
            evictCache(joinPoint, cacheEvict);
            result = joinPoint.proceed();
        } else {
            result = joinPoint.proceed();
            evictCache(joinPoint, cacheEvict);
        }
        
        return result;
    }
    
    private void evictCache(ProceedingJoinPoint joinPoint, @NonNull CacheEvict cacheEvict) {
        if (cacheEvict.allEntries()) {
            if (!cacheEvict.key().isEmpty()) {
                // Evict by pattern
                String pattern = keyGenerator.generateKey(
                        joinPoint,
                        cacheEvict.key(),
                        cacheEvict.keyPrefix(),
                        false,
                        false,
                        false
                );

                resourceManagementCacheService.evictByPattern(pattern);
                log.debug("{} Evicted cache entries by pattern: {}", LOG_NAME, pattern);
            } else {
                // Evict all
                resourceManagementCacheService.evictAll();
                log.debug("{} Evicted all cache entries", LOG_NAME);
            }
        } else {
            String key = keyGenerator.generateKey(
                    joinPoint,
                    cacheEvict.key(),
                    cacheEvict.keyPrefix(),
                    cacheEvict.includeMethodName(),
                    cacheEvict.includeClassName(),
                    cacheEvict.includeParameters()
            );

            resourceManagementCacheService.evict(key);
            log.debug("{} Evicted cache entry for key: {}", LOG_NAME, key);
        }
    }
}