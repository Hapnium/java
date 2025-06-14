package com.hapnium.core.resourcemanagement.rate_limit;

import com.hapnium.core.resourcemanagement.ResourceManagementKeyGenerator;
import com.hapnium.core.resourcemanagement.UserResourceManagementProvider;
import com.hapnium.core.resourcemanagement.rate_limit.annotations.RateLimited;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

/**
 * Utility class responsible for generating unique rate limit keys for methods annotated with rate-limiting annotations.
 * Generates rate limit keys using method/class names, user/IP info, and SpEL expressions.
 *
 * <p>
 * Inherits common key generation logic from {@link ResourceManagementKeyGenerator}.
 * </p>
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
@Component
class ResourceManagementRateLimitKeyGenerator extends ResourceManagementKeyGenerator {
    public ResourceManagementRateLimitKeyGenerator(UserResourceManagementProvider provider) {
        super(true, true, "[RATE_LIMIT_KEY GENERATOR]:", provider);
    }

    public String generateKey(ProceedingJoinPoint joinPoint, @NonNull RateLimited rateLimited) {
        StringJoiner keyBuilder = new StringJoiner(":");

        // Add common key parts
        addCommonKeyComponents(
                keyBuilder,
                joinPoint,
                rateLimited.keyPrefix(),
                rateLimited.includeClassName(),
                rateLimited.includeMethodName()
        );

        // Add rate limit specific components
        addRateLimitSpecificComponents(keyBuilder, rateLimited);

        // Process custom key with SpEL if specified
        String customKey = evaluateSpelExpression(rateLimited.key(), joinPoint);
        if (customKey != null) {
            keyBuilder.add(customKey);
        }

        String finalKey = buildKey(keyBuilder, createDefaultKey(joinPoint));

        log.debug("{} Generated rate limit key: {}", LOG_NAME, finalKey);
        return finalKey;
    }

    private void addRateLimitSpecificComponents(StringJoiner builder, @NonNull RateLimited limited) {
        // Add user identifier if requested
        if (limited.includeUser()) {
            String userId = getCurrentUserId();
            if (userId != null) {
                builder.add("user:" + userId);
            }
        }

        // Add IP address if requested
        if (limited.includeIpAddress()) {
            String ipAddress = getCurrentIpAddress();
            if (ipAddress != null) {
                builder.add("ip:" + ipAddress);
            }
        }
    }
}