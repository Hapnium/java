package com.hapnium.core.resourcemanagement.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * Indicates that the result of invoking a method (with given arguments) can be cached.
 * <p>
 * When applied to a method, the result will be stored in the configured cache system based on the provided key configuration.
 * This annotation supports rich key construction using SpEL, as well as fine-grained control over TTL, null value caching,
 * and conditional caching.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * @Cacheable(
 *     key = "#user.id",
 *     keyPrefix = "user-cache:",
 *     ttl = 15,
 *     ttlUnit = ChronoUnit.MINUTES,
 *     cacheNull = false,
 *     condition = "#user.active",
 *     unless = "#result == null"
 * )
 * public User getUser(User user) {
 *     return userService.fetch(user.getId());
 * }
 * }</pre>
 *
 * <p>This annotation is processed at runtime and is designed to work with Hapnium's pluggable caching system.</p>
 *
 * @author Evaristus Adimonyemma
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    /**
     * Specifies the cache key using a Spring Expression Language (SpEL) expression.
     * <p>
     * If left empty, the key will be constructed based on method parameters and other flags.
     * Example: {@code "#user.id"}.
     *
     * @return SpEL expression for the cache key.
     */
    String key() default "";

    /**
     * Prefix to prepend to the generated cache key.
     * Useful for namespace separation between cache groups.
     *
     * @return Key prefix string.
     */
    String keyPrefix() default "";

    /**
     * Whether to include the method name in the generated cache key.
     * Recommended for disambiguating overloaded methods or shared logic.
     *
     * @return {@code true} to include the method name in the cache key.
     */
    boolean includeMethodName() default true;

    /**
     * Whether to include the class name in the generated cache key.
     * Useful in multi-class environments to ensure key uniqueness.
     *
     * @return {@code true} to include the class name in the cache key.
     */
    boolean includeClassName() default false;

    /**
     * Whether to include method parameters in the cache key.
     * This is usually necessary to generate a unique key per argument set.
     *
     * @return {@code true} to include method parameters in the cache key.
     */
    boolean includeParameters() default true;

    /**
     * Time-to-live (TTL) value for the cached entry.
     * If {@code 0}, the system-wide default TTL will be applied.
     *
     * @return TTL duration value.
     */
    long ttl() default 0;

    /**
     * Unit of time for the TTL value.
     * Used in conjunction with {@link #ttl()}.
     *
     * @return Time unit for TTL.
     */
    ChronoUnit ttlUnit() default ChronoUnit.MINUTES;

    /**
     * Indicates whether {@code null} results should be cached.
     * By default, {@code false}, which means {@code null} values will be ignored.
     *
     * @return {@code true} if {@code null} results should be cached.
     */
    boolean cacheNull() default false;

    /**
     * SpEL expression used to conditionally cache method results.
     * The method result will only be cached if this expression evaluates to {@code true}.
     * <p>Example: {@code "#user.active"}</p>
     *
     * @return SpEL expression for caching condition.
     */
    String condition() default "";

    /**
     * SpEL expression that, when evaluated to {@code true}, will prevent the result from being cached.
     * <p>Example: {@code "#result == null"}</p>
     *
     * @return SpEL expression for "unless" condition.
     */
    String unless() default "";
}