package com.hapnium.core.resourcemanagement.rate_limit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * Annotation to apply rate limiting to methods or entire classes.
 * <p>
 * When applied, the annotated method or all public methods within the annotated class will be subject
 * to the configured rate limit constraints.
 * </p>
 *
 * <p>
 * Rate limiting helps prevent abuse and ensures fair usage of system resources by limiting the number of
 * invocations over a defined time window.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * @RateLimited(
 *     limit = 10,
 *     window = 1,
 *     windowUnit = ChronoUnit.MINUTES,
 *     key = "#user.id",
 *     includeIpAddress = true,
 *     strategy = "sliding-window"
 * )
 * public ResponseEntity<?> handleRequest(User user) {
 *     // method logic
 * }
 * }</pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    /**
     * Maximum number of allowed requests within the specified time window.
     *
     * @return The request limit.
     */
    int limit() default 100;

    /**
     * Duration value for the time window used in rate limiting.
     *
     * @return Window duration.
     */
    long window() default 1;

    /**
     * Time unit for the {@link #window()} duration.
     * Supported units include {@code SECONDS}, {@code MINUTES}, {@code HOURS}, etc.
     *
     * @return Time unit for the rate limit window.
     */
    ChronoUnit windowUnit() default ChronoUnit.MINUTES;

    /**
     * Strategy to use for rate limiting.
     * Supported values include:
     * <ul>
     *   <li>{@code sliding-window}</li>
     *   <li>{@code fixed-window}</li>
     *   <li>{@code token-bucket}</li>
     * </ul>
     *
     * @return Rate limiting strategy.
     */
    String strategy() default "sliding-window";

    /**
     * Key expression used to compute a unique identifier for rate limiting.
     * Can be a Spring Expression Language (SpEL) expression referencing method parameters.
     * If not provided, the key will be derived from other attributes.
     *
     * @return SpEL expression for rate limit key.
     */
    String key() default "";

    /**
     * Optional prefix to prepend to the rate limit key.
     * Useful for namespace separation or grouping.
     *
     * @return Prefix for the key.
     */
    String keyPrefix() default "";

    /**
     * Whether to include the method name in the generated rate limit key.
     * Helps differentiate rate limits across methods.
     *
     * @return {@code true} to include the method name.
     */
    boolean includeMethodName() default true;

    /**
     * Whether to include the class name in the generated rate limit key.
     * Useful to avoid key collisions across different components.
     *
     * @return {@code true} to include the class name.
     */
    boolean includeClassName() default false;

    /**
     * Whether to include the authenticated user’s identifier in the rate limit key.
     * Requires an authentication context that provides user identity.
     *
     * @return {@code true} to include user ID.
     */
    boolean includeUser() default false;

    /**
     * Whether to include the requester's IP address in the rate limit key.
     * Useful for per-client or per-device rate limiting.
     *
     * @return {@code true} to include IP address.
     */
    boolean includeIpAddress() default false;

    /**
     * Message returned or logged when the rate limit is exceeded.
     * Can be used in exception handling or user feedback.
     *
     * @return Message shown on limit violation.
     */
    String message() default "Rate limit exceeded. Please try again later.";

    /**
     * Whether to skip rate limiting if an internal error occurs during key resolution or enforcement.
     * Useful for high-availability systems where failure should not block the request.
     *
     * @return {@code true} to skip rate limiting on failure.
     */
    boolean skipOnFailure() default true;
}