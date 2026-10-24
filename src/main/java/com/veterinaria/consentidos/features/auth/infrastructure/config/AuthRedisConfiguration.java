package com.veterinaria.consentidos.features.auth.infrastructure.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PreDestroy;
import java.time.Duration;

@Configuration
@Profile("!local | external-storage")
public class AuthRedisConfiguration {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;

    @Bean
    public RedisClient redisClient(RedisAuthProperties properties) {
        if (properties.getHost() == null || properties.getHost().isBlank()) {
            throw new IllegalStateException("Redis host is required for non-local profiles");
        }

        RedisURI.Builder builder = RedisURI.Builder.redis(properties.getHost(), properties.getPort())
            .withDatabase(properties.getDatabase())
            .withTimeout(Duration.ofSeconds(properties.getTimeoutSeconds()));

        if (properties.isSsl()) {
            builder.withSsl(true);
        }

        if (properties.getUsername() != null && !properties.getUsername().isBlank()) {
            builder.withAuthentication(properties.getUsername(), properties.getPassword() == null ? "" : properties.getPassword());
        }

        this.redisClient = RedisClient.create(builder.build());
        return this.redisClient;
    }

    @Bean
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        this.redisConnection = redisClient.connect();
        return this.redisConnection;
    }

    @PreDestroy
    public void shutdown() {
        if (redisConnection != null) {
            redisConnection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}


