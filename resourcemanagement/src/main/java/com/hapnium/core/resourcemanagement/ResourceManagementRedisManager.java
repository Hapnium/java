package com.hapnium.core.resourcemanagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(name = {"hapnium.resourcemanagement.cache.provider", "hapnium.resourcemanagement.rate-limit.provider"}, havingValue = "redis")
class ResourceManagementRedisManager {
    private final String LOG_NAME = "[HAPNIUM-RESOURCEMANAGEMENT REDIS MANAGER]:";

    @Bean
    @ConditionalOnMissingBean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        String redisHost = getProperty("spring.data.redis.host", "localhost");
        int redisPort = Integer.parseInt(getProperty("spring.data.redis.port", "6379"));
        String redisPassword = getProperty("spring.data.redis.password", "");
        int redisDatabase = Integer.parseInt(getProperty("spring.data.redis.database", "0"));

        log.info("{} Configuring Redis connection to {}:{} database {}", LOG_NAME, redisHost, redisPort, redisDatabase);

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setDatabase(redisDatabase);

        if (redisPassword != null && !redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }

        return redisConfig;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public LettuceConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {
        try {
            LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration);
            factory.afterPropertiesSet();

            log.info("{} Redis connection factory created successfully", LOG_NAME);
            return factory;
        } catch (Exception e) {
            log.error("{} Failed to create Redis connection factory", LOG_NAME, e);
            throw e;
        }
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Enable transaction support
        template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();
        log.info("{} Redis template created successfully", LOG_NAME);

        // Test connection
        boolean value = testConnection(template);
        log.info("{} Redis connection test result: {}", LOG_NAME, value);

        return template;
    }

    private static String getProperty(String key, String defaultValue) {
        // First try system properties, then environment variables
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }

        // Convert a property key to environment variable format
        String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
        value = System.getenv(envKey);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    // Test Redis connection
    private boolean testConnection(RedisTemplate<String, Object> template) {
        try {
            template.opsForValue().set("test:connection", "test", Duration.ofSeconds(10));
            String result = (String) template.opsForValue().get("test:connection");
            template.delete("test:connection");

            if ("test".equals(result)) {
                log.info("{} Redis connection test successful", LOG_NAME);
                return true;
            } else {
                log.warn("{} Redis connection test failed: unexpected result", LOG_NAME);
                return false;
            }
        } catch (Exception e) {
            log.error("{} Redis connection test failed", LOG_NAME, e);
            return false;
        }
    }
}