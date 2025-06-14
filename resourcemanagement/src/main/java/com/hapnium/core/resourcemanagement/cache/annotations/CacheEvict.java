package com.hapnium.core.resourcemanagement.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that one or more cache entries should be removed when the annotated method is invoked.
 * <p>
 * This annotation allows fine-grained control over cache eviction based on method parameters, SpEL conditions,
 * and optional timing (before or after method execution). It supports both selective key-based eviction
 * and complete cache clearance.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * @CacheEvict(
 *     key = "#user.id",
 *     keyPrefix = "user-cache:",
 *     allEntries = false,
 *     beforeInvocation = false,
 *     condition = "#user.deactivated"
 * )
 * public void deactivateUser(User user) {
 *     userService.deactivate(user);
 * }
 * }</pre>
 *
 * <p>This annotation is runtime-retained and processed by Hapnium's caching infrastructure.</p>
 *
 * @author Evaristus Adimonyemma
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvict {
    /**
     * Specifies the cache key using a Spring Expression Language (SpEL) expression.
     * If omitted, the key will be generated using the method name, class name, and parameters as configured.
     * <p>Example: {@code "#user.id"}</p>
     *
     * @return SpEL expression to compute the cache key.
     */
    String key() default "";

    /**
     * Optional prefix to prepend to the generated cache key.
     * Useful for namespacing or organizing cache keys logically.
     *
     * @return Key prefix string.
     */
    String keyPrefix() default "";

    /**
     * Whether to include the method name in the generated cache key.
     * Helps disambiguate between methods in different contexts or overloads.
     *
     * @return {@code true} to include the method name in the cache key.
     */
    boolean includeMethodName() default true;

    /**
     * Whether to include the class name in the generated cache key.
     * Useful for enforcing global uniqueness across multiple classes.
     *
     * @return {@code true} to include the class name in the cache key.
     */
    boolean includeClassName() default false;

    /**
     * Whether to include method parameters in the generated cache key.
     * Generally recommended for identifying which specific cache entries to evict.
     *
     * @return {@code true} to include method parameters in the cache key.
     */
    boolean includeParameters() default true;

    /**
     * Indicates whether all entries in the cache namespace (given by {@link #keyPrefix()}) should be evicted.
     * <p>If {@code true}, the individual key and parameters are ignored.</p>
     *
     * @return {@code true} to evict all entries under the specified key prefix.
     */
    boolean allEntries() default false;

    /**
     * Whether eviction should occur before the annotated method is invoked.
     * <ul>
     *   <li>{@code true} — evict before the method is executed.</li>
     *   <li>{@code false} — evict only if the method successfully completes.</li>
     * </ul>
     *
     * @return {@code true} to evict before method execution.
     */
    boolean beforeInvocation() default false;

    /**
     * SpEL expression that determines whether the eviction should proceed.
     * If the condition evaluates to {@code true}, the cache is evicted. Otherwise, it is skipped.
     * <p>Example: {@code "#user.deactivated"}</p>
     *
     * @return SpEL expression to evaluate as a condition for eviction.
     */
    String condition() default "";
}