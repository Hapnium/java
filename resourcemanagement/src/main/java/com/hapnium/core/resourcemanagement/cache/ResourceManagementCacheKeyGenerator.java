package com.hapnium.core.resourcemanagement.cache;

import com.hapnium.core.resourcemanagement.ResourceManagementKeyGenerator;
import com.hapnium.core.resourcemanagement.UserResourceManagementProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

/**
 * Utility class responsible for generating unique cache keys for methods annotated with caching annotations.
 * <p>
 * This generator supports flexible key creation using various options such as class name, method name,
 * parameters, and custom SpEL-based expressions.
 * </p>
 * <p>
 * Inherits common key generation logic from {@link ResourceManagementKeyGenerator}.
 * </p>
 *
 * @author Evaristus Adimonyemma
 */
@Slf4j
@Component
class ResourceManagementCacheKeyGenerator extends ResourceManagementKeyGenerator {
    /**
     * Constructs a new {@code CacheKeyGenerator} with the specified {@link UserResourceManagementProvider}.
     *
     * @param provider A provider supplying user/resource context to help generate contextual cache keys.
     */
    public ResourceManagementCacheKeyGenerator(UserResourceManagementProvider provider) {
        super(false, false, "[CACHE_KEY GENERATOR]:", provider);
    }

    /**
     * Generates a cache key for a method invocation based on specified configuration and parameters.
     *
     * @param joinPoint         The AOP join point representing the intercepted method call.
     * @param keyExpression     An optional SpEL expression used to compute part of the key.
     * @param keyPrefix         A static prefix to prepend to the key.
     * @param includeMethodName Whether to include the method name in the generated key.
     * @param includeClassName  Whether to include the class name in the generated key.
     * @param includeParameters Whether to include method parameters in the key if no custom SpEL is used.
     * @return A uniquely constructed cache key as a {@link String}.
     */
    public String generateKey(
            ProceedingJoinPoint joinPoint,
            String keyExpression,
            @NonNull String keyPrefix,
            boolean includeMethodName,
            boolean includeClassName,
            boolean includeParameters
    ) {
        StringJoiner keyBuilder = new StringJoiner(":");

        // Add static or contextual key elements like class and method names
        addCommonKeyComponents(keyBuilder, joinPoint, keyPrefix, includeClassName, includeMethodName);

        // Evaluate SpEL expression if provided
        String customKey = evaluateSpelExpression(keyExpression, joinPoint);
        if (customKey != null) {
            keyBuilder.add(customKey);
        }

        // If no custom key, append method parameters as part of the key
        if (includeParameters && (keyExpression == null || keyExpression.isEmpty())) {
            String parametersKey = generateParametersKey(joinPoint.getArgs());
            keyBuilder.add(parametersKey);
        }

        // Construct a fallback key if none of the above yielded a usable key
        String defaultKey = createDefaultKey(joinPoint) + ":" + generateParametersKey(joinPoint.getArgs());

        // Build final key with fallbacks handled
        String finalKey = buildKey(keyBuilder, defaultKey);

        log.debug("{} Generated cache key: {}", LOG_NAME, finalKey);

        return finalKey;
    }
}