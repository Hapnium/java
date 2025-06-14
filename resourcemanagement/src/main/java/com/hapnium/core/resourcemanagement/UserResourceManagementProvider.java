package com.hapnium.core.resourcemanagement;

/**
 * A functional interface that must be implemented by any application using the
 * resource management library to provide access to the currently authenticated user's ID.
 * <p>
 * This interface decouples the library from any specific authentication or user context implementation,
 * allowing the consuming application to define how and where the user ID is retrieved from—such as
 * a Spring Security context, a thread-local storage, or a custom authentication service.
 * </p>
 *
 * <p>
 * The {@code UserResourceManagementProvider} is typically exposed as a Spring {@code @Bean}
 * in the consuming application. The resource management library will automatically detect and
 * inject this bean to retrieve user-specific information for features like caching or rate limiting.
 * </p>
 *
 * <pre>{@code
 * // Example usage in a Spring Boot application
 *
 * @Configuration
 * public class ResourceManagementConfig {
 *
 *     @Bean
 *     public UserResourceManagementProvider userResourceManagementProvider() {
 *         return () -> SecurityContextHolder.getContext()
 *                                           .getAuthentication()
 *                                           .getName(); // or any other custom logic
 *     }
 * }
 * }</pre>
 *
 * @author Evaristus Adimonyemma
 * @since 1.0
 */
@FunctionalInterface
public interface UserResourceManagementProvider {
    /**
     * Returns the unique identifier of the currently authenticated user.
     * <p>
     * This method is expected to return a stable, unique value (e.g., user ID, UUID, or username)
     * representing the current user, which will be used by the resource management features for
     * purposes such as key generation in caching or rate limiting contexts.
     * </p>
     *
     * <p>
     * If no user is authenticated (e.g., in a public or anonymous request),
     * this method may return {@code null}. The resource management framework should
     * handle {@code null} values appropriately (e.g., fallback behavior).
     * </p>
     *
     * @return the current user's unique ID, or {@code null} if unauthenticated
     */
    String getCurrentUserId();
}