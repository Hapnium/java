package com.hapnium.core.resourcemanagement.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * Indicates that the result of invoking a method (or the method's return value) should be stored in the cache,
 * regardless of whether a cache entry already exists.
 *
 * <p>
 * Unlike {@code @Cacheable}, which skips method execution if the cache contains the value,
 * {@code @CachePut} always executes the method and updates the cache with the returned result.
 * This is useful for scenarios where data should be forcibly updated in the cache, such as after an update or create operation.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * @CachePut(
 *     key = "#user.id",
 *     keyPrefix = "user-cache:",
 *     ttl = 10,
 *     ttlUnit = ChronoUnit.MINUTES
 * )
 * public User updateUser(User user) {
 *     return userRepository.save(user);
 * }
 * }</pre>
 *
 * <p>This annotation is runtime-retained and processed by Hapnium's caching system.</p>
 *
 * @author Evaristus Adimonyemma
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {
    /**
     * Specifies the cache key using a Spring Expression Language (SpEL) expression.
     * If omitted, the key will be constructed using method name, class name, and parameters based on the flags.
     * <p>Example: {@code "#user.id"}</p>
     *
     * @return SpEL expression for computing the cache key.
     */
    String key() default "";

    /**
     * Prefix to prepend to the cache key.
     * Useful for logical grouping or isolation of cache keys.
     *
     * @return Prefix for the cache key.
     */
    String keyPrefix() default "";

    /**
     * Whether to include the method name in the generated cache key.
     * Helps differentiate keys across overloaded methods or similar signatures.
     *
     * @return {@code true} to include the method name in the key.
     */
    boolean includeMethodName() default true;

    /**
     * Whether to include the class name in the cache key.
     * Helps ensure global uniqueness across different classes.
     *
     * @return {@code true} to include the class name in the key.
     */
    boolean includeClassName() default false;

    /**
     * Whether to include method parameters in the cache key.
     * Generally recommended when caching is dependent on input values.
     *
     * @return {@code true} to include parameters in the key.
     */
    boolean includeParameters() default true;

    /**
     * Time-to-live value for the cache entry. A value of {@code 0} indicates that the default TTL should be used.
     *
     * @return TTL duration value.
     */
    long ttl() default 0;

    /**
     * Time-to-live unit for the {@link #ttl()} value.
     * Defaults to {@code ChronoUnit.MINUTES}.
     *
     * @return Time unit for the TTL.
     */
    ChronoUnit ttlUnit() default ChronoUnit.MINUTES;

    /**
     * Whether {@code null} values should be cached.
     * Useful when you want to avoid repeated lookups for known-null results.
     *
     * @return {@code true} to allow caching of {@code null} values.
     */
    boolean cacheNull() default false;

    /**
     * SpEL expression that determines whether the method result should be cached.
     * If the expression evaluates to {@code true}, the result will be cached.
     * <p>Example: {@code "#result != null"}</p>
     *
     * @return Condition under which the result should be cached.
     */
    String condition() default "";

    /**
     * SpEL expression that determines whether caching should be skipped.
     * If this expression evaluates to {@code true}, the result will not be cached.
     * <p>Example: {@code "#result.size() == 0"}</p>
     *
     * @return Condition under which caching should be skipped.
     */
    String unless() default "";
}