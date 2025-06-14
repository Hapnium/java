# Hapnium Cache and Rate Limit Spring Boot Starter

A comprehensive Spring Boot starter that provides caching and rate limiting functionality with multiple provider support.

## Features

### Caching
- **Multiple Providers**: Caffeine (default), Redis, Simple in-memory
- **Annotations**: `@Cacheable`, `@CachePut`, `@CacheEvict`
- **Async Support**: Asynchronous cache operations
- **TTL Management**: Configurable time-to-live for cache entries
- **Pattern-based Eviction**: Evict cache entries by pattern matching

### Rate Limiting
- **Multiple Providers**: Memory (default), Redis
- **Strategies**: Sliding window, Token bucket, Fixed window
- **Flexible Configuration**: Per-endpoint and per-user-type limits
- **SpEL Support**: Dynamic key generation with Spring Expression Language
- **User Context**: Automatic user ID and IP address inclusion

## Quick Start

### 1. Add Dependency

\`\`\`xml
<dependency>
    <groupId>com.hapnium.core</groupId>
    <artifactId>resourcemanagement</artifactId>
    <version>1.0.0</version>
</dependency>
\`\`\`

### 2. Configuration

\`\`\`yaml
hapnium:
  cache:
    enabled: true
    provider: caffeine  # caffeine, redis, memory
    default-ttl: PT5M
    key-prefix: "myapp:cache:"
    
  rate-limit:
    enabled: true
    provider: memory    # memory, redis
    default-strategy: sliding-window
    default-limit: 100
    default-window: PT1M
    key-prefix: "myapp:rl:"
\`\`\`

### 3. Usage Examples

#### Caching

\`\`\`java
@Service
public class UserService {
    
    @Cacheable(key = "#userId", ttl = 30, ttlUnit = ChronoUnit.MINUTES)
    public User getUserById(String userId) {
        // Expensive operation
        return userRepository.findById(userId);
    }
    
    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @CacheEvict(key = "#userId")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
\`\`\`

#### Rate Limiting

\`\`\`java
@RestController
public class ApiController {
    
    @RateLimit(limit = 10, window = 1, windowUnit = ChronoUnit.MINUTES)
    @GetMapping("/api/data")
    public ResponseEntity<String> getData() {
        return ResponseEntity.ok("Data");
    }
    
    @RateLimit(
        key = "#request.endpoint", 
        limit = 5, 
        window = 1, 
        includeUserId = true,
        message = "Too many login attempts"
    )
    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // Login logic
        return ResponseEntity.ok("Success");
    }
}
\`\`\`

## Advanced Configuration

### Cache Configuration

\`\`\`yaml
hapnium:
  cache:
    provider: redis
    caches:
      user-profile:
        ttl: PT30M
        max-size: 1000
        enable-refresh: true
        refresh-after-write: PT15M
      activity-data:
        ttl: PT10M
        max-size: 5000
    redis:
      key-prefix: "myapp:cache:"
      enable-compression: true
      compression-threshold: 1024
    caffeine:
      maximum-size: 10000
      expire-after-write: PT10M
      expire-after-access: PT5M
\`\`\`

### Rate Limit Configuration

\`\`\`yaml
hapnium:
  rate-limit:
    endpoints:
      "/api/auth/login":
        enabled: true
        limit: 5
        window: PT1M
        strategy: sliding-window
        user-type-limits:
          premium: 10
          basic: 5
          guest: 3
      "/api/search":
        limit: 200
        window: PT1M
        strategy: token-bucket
    user-types:
      premium:
        enabled: true
        limit: 1000
        window: PT1H
        strategy: token-bucket
      basic:
        limit: 500
        window: PT1H
        strategy: sliding-window
\`\`\`

## Redis Configuration

When using Redis as a provider, configure your Redis connection:

\`\`\`yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your-password
      database: 0
\`\`\`

## Programmatic Usage

### Cache Service

\`\`\`java
@Autowired
private CacheService cacheService;

public void example() {
    // Store value
    cacheService.put("key", "value", Duration.ofMinutes(10));
    
    // Retrieve value
    Optional<String> value = cacheService.get("key", String.class);
    
    // Async operations
    cacheService.putAsync("key", "value", Duration.ofMinutes(5));
    CompletableFuture<Optional<String>> futureValue = cacheService.getAsync("key", String.class);
    
    // Eviction
    cacheService.evict("key");
    cacheService.evictByPattern("user:*");
    cacheService.evictAll();
}
\`\`\`

### Rate Limit Service

\`\`\`java
@Autowired
private RateLimitService rateLimitService;

public void example() {
    // Check rate limit
    boolean allowed = rateLimitService.isAllowed("user:123", 10, Duration.ofMinutes(1));
    
    // Get remaining attempts
    long remaining = rateLimitService.getRemainingAttempts("user:123", 10, Duration.ofMinutes(1));
    
    // Reset rate limit
    rateLimitService.reset("user:123");
}
\`\`\`

## License

This project is licensed under the MIT License.
