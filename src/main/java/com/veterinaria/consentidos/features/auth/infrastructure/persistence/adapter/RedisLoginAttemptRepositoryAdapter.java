package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptPolicy;
import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptState;
import com.veterinaria.consentidos.features.auth.domain.repository.LoginAttemptRepository;
import com.veterinaria.consentidos.features.auth.infrastructure.config.RedisAuthProperties;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("!local | external-storage")
public class RedisLoginAttemptRepositoryAdapter extends AbstractLoginAttemptRepositoryAdapter implements LoginAttemptRepository {

    private static final String PREFIX = "login:attempt:";

    private final StatefulRedisConnection<String, String> connection;
    private final RedisAuthProperties properties;

    public RedisLoginAttemptRepositoryAdapter(
        StatefulRedisConnection<String, String> connection,
        RedisAuthProperties properties
    ) {
        this.connection = connection;
        this.properties = properties;
    }

    @Override
    public Optional<LoginAttemptState> findByKey(String key) {
        RedisCommands<String, String> commands = connection.sync();
        Map<String, String> values = commands.hgetall(redisKey(key));
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }

        LoginAttemptState state = fromHash(key, values);
        if (state.isExpired(Instant.now())) {
            commands.del(redisKey(key));
            return Optional.empty();
        }
        return Optional.of(state);
    }

    @Override
    public void registerFailure(
        String key,
        String userHint,
        String deviceId,
        String clientIp,
        String userAgent,
        LoginAttemptPolicy policy
    ) {
        RedisCommands<String, String> commands = connection.sync();
        Instant now = Instant.now();
        Optional<LoginAttemptState> current = findByKey(key);

        Instant windowStartedAt = current.map(LoginAttemptState::windowStartedAt).orElse(now);
        int attempts = current.map(LoginAttemptState::attempts).orElse(0) + 1;
        Instant blockedUntil = attempts >= policy.maxAttempts() ? now.plus(policy.lockout()) : null;
        Instant expiresAt = blockedUntil != null ? blockedUntil : windowStartedAt.plus(policy.window());

        LoginAttemptState state = buildState(new LoginAttemptBuildInput(
            key,
            userHint,
            deviceId,
            clientIp,
            userAgent,
            attempts,
            windowStartedAt,
            now,
            blockedUntil,
            expiresAt
        ));

        commands.hset(redisKey(key), toStorageMap(state));
        commands.expire(redisKey(key), ttlSeconds(state.expiresAt()));
    }

    @Override
    public void clear(String key) {
        connection.sync().del(redisKey(key));
    }

    private LoginAttemptState fromHash(String key, Map<String, String> values) {
        return new LoginAttemptState(
            key,
            blankToNull(values.get("userHint")),
            blankToNull(values.get("deviceId")),
            blankToNull(values.get("ipHash")),
            blankToNull(values.get("userAgentHash")),
            Integer.parseInt(values.getOrDefault("attempts", "0")),
            Instant.parse(values.get("windowStartedAt")),
            Instant.parse(values.get("lastAttemptAt")),
            parseInstant(values.get("blockedUntil")),
            Instant.parse(values.get("expiresAt"))
        );
    }

    private String redisKey(String key) {
        return properties.getKeyPrefix() + ":" + PREFIX + key;
    }

    private long ttlSeconds(Instant expiresAt) {
        long seconds = Duration.between(Instant.now(), expiresAt).getSeconds();
        return Math.max(1L, seconds);
    }

}

