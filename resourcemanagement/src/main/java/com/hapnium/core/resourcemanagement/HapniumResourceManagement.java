package com.hapnium.core.resourcemanagement;

import com.hapnium.core.resourcemanagement.cache.ResourceManagementCacheManager;
import com.hapnium.core.resourcemanagement.rate_limit.ResourceManagementRateLimitManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration entry point for Hapnium's Resource Management module.
 * <p>
 * This configuration class bootstraps core functionality related to:
 * <ul>
 *   <li>Request rate limiting</li>
 *   <li>Flexible cache management</li>
 *   <li>Custom Redis integrations (if available)</li>
 * </ul>
 *
 * <p>
 * It enables support for both in-memory and Redis-backed rate limit and cache providers based on
 * Spring Boot configuration properties. All major service components (e.g., aspects, providers,
 * services, and key generators) are automatically scanned and registered.
 * </p>
 *
 * <h3>Main Responsibilities</h3>
 * <ul>
 *   <li>Imports sub-configurations for cache, rate limiting, and Redis management</li>
 *   <li>Registers {@link ResourceManagementProperty} for configuration binding</li>
 *   <li>Provides a fallback {@link UserResourceManagementProvider} if none is defined</li>
 * </ul>
 *
 * <h3>Default User Provider</h3>
 * <p>
 * If no {@link UserResourceManagementProvider} bean is found in the application context,
 * a default no-op implementation is provided that returns {@code null}.
 * This can be overridden to supply contextual information such as user ID, organization, or tenant.
 * </p>
 *
 * <h3>Imported Configurations</h3>
 * <ul>
 *   <li>{@link ResourceManagementCacheManager} - Sets up cache strategies and beans</li>
 *   <li>{@link ResourceManagementRateLimitManager} - Configures rate limiting strategies</li>
 *   <li>{@link ResourceManagementRedisManager} - Initializes Redis-specific settings</li>
 * </ul>
 *
 * <h3>Component Scanning</h3>
 * <p>
 * Automatically scans and registers components under the following packages:
 * </p>
 * <ul>
 *   <li>{@code com.hapnium.core.resourcemanagement.cache}</li>
 *   <li>{@code com.hapnium.core.resourcemanagement.rate_limit}</li>
 * </ul>
 *
 * <p>
 * This class is annotated with {@link org.springframework.boot.autoconfigure.AutoConfiguration},
 * allowing Spring Boot to automatically apply it during startup if the module is on the classpath.
 * </p>
 *
 * @author Evaristus Adimonyemma
 * @see ResourceManagementProperty
 * @see UserResourceManagementProvider
 * @see ResourceManagementCacheManager
 * @see ResourceManagementRateLimitManager
 * @see ResourceManagementRedisManager
 */
@Slf4j
@AutoConfiguration
@Import({
		ResourceManagementRedisManager.class,
		ResourceManagementRateLimitManager.class,
		ResourceManagementCacheManager.class
})
@ComponentScan(basePackages = {
		"com.hapnium.core.resourcemanagement.rate_limit",
		"com.hapnium.core.resourcemanagement.cache"
})
public class HapniumResourceManagement {
	@Bean
	@ConditionalOnMissingBean(UserResourceManagementProvider.class)
	public UserResourceManagementProvider defaultCurrentUserProvider() {
		log.info("Using default user provider");
		return () -> null;
	}
}