package com.hapnium.core.resourcemanagement.rate_limit;

import com.hapnium.core.resourcemanagement.exception.RateLimitExceededException;
import com.hapnium.core.resourcemanagement.rate_limit.annotations.RateLimited;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitRequest;
import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Aspect for intercepting method invocations annotated with {@link com.hapnium.core.resourcemanagement.rate_limit.annotations.RateLimited}.
 * <p>
 * This aspect uses AOP to apply rate limiting logic declaratively, without modifying the target methods.
 * It leverages {@link ResourceManagementRateLimitService} and {@link ResourceManagementRateLimitKeyGenerator}
 * to dynamically evaluate keys and enforce limits.
 * </p>
 *
 * <p>Main Responsibilities:</p>
 * <ul>
 *   <li>Intercept method calls annotated with {@code @RateLimited}</li>
 *   <li>Generate a unique key based on method context, user, and IP address</li>
 *   <li>Delegate rate limit enforcement to the configured provider</li>
 *   <li>Throw appropriate exceptions or allow the call to proceed based on provider response</li>
 * </ul>
 *
 * <p>
 * This component is automatically enabled if {@code hapnium.resourcemanagement.rate-limit.enabled=true}.
 * </p>
 *
 * @see com.hapnium.core.resourcemanagement.rate_limit.annotations.RateLimited
 * @see ResourceManagementRateLimitService
 * @see ResourceManagementRateLimitKeyGenerator
 */
@Slf4j
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
class ResourceManagementRateLimitAspect {
    private final ResourceManagementRateLimitService rateLimitService;
    private final ResourceManagementRateLimitKeyGenerator keyGenerator;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = keyGenerator.generateKey(joinPoint, rateLimited);
        Duration window = Duration.of(rateLimited.window(), rateLimited.windowUnit());
        
        RateLimitRequest request = RateLimitRequest.builder()
                .key(key)
                .limit(rateLimited.limit())
                .window(window)
                .strategy(rateLimited.strategy())
                .build();
        
        RateLimitResult result = rateLimitService.checkRateLimit(request);

        String LOG_NAME = "[RATE_LIMIT ASPECT]:";

        if (!result.isAllowed()) {
            log.warn("{} Rate limit exceeded for key: {}, limit: {}, window: {}", LOG_NAME, key, rateLimited.limit(), window);
            throw new RateLimitExceededException(rateLimited.message(), result);
        }
        
        log.debug("{} Rate limit check passed for key: {}, remaining: {}", LOG_NAME, key, result.getRemainingRequests());
        return joinPoint.proceed();
    }

    @Around("@within(rateLimited)")
    public Object rateLimitClass(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        return rateLimit(joinPoint, rateLimited);
    }
}