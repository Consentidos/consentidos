package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.RefreshTokenSession;
import com.veterinaria.consentidos.features.auth.domain.repository.RefreshTokenSessionRepository;
import com.veterinaria.consentidos.features.auth.infrastructure.config.RedisAuthProperties;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("!local | external-storage")
public class RedisRefreshTokenSessionRepositoryAdapter implements RefreshTokenSessionRepository {

    private static final String SESSION_PREFIX = "refresh:session:";
    private static final String ACTIVE_USER_PREFIX = "refresh:active-user:";

    private final StatefulRedisConnection<String, String> connection;
    private final RedisAuthProperties properties;

    public RedisRefreshTokenSessionRepositoryAdapter(
        StatefulRedisConnection<String, String> connection,
        RedisAuthProperties properties
    ) {
        this.connection = connection;
        this.properties = properties;
    }

    @Override
    public void save(RefreshTokenSession session) {
        RedisCommands<String, String> commands = connection.sync();
        String sessionKey = sessionKey(session.tokenId());
        String activeUserKey = activeUserKey(session.userId());

        commands.hset(sessionKey, toHash(session));
        commands.expire(sessionKey, ttlSeconds(session.expiresAt()));
        commands.set(activeUserKey, session.tokenId());
        commands.expire(activeUserKey, ttlSeconds(session.expiresAt()));
    }

    @Override
    public Optional<RefreshTokenSession> findByTokenId(String tokenId) {
        RedisCommands<String, String> commands = connection.sync();
        Map<String, String> values = commands.hgetall(sessionKey(tokenId));
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(fromHash(values));
    }

    @Override
    public Optional<RefreshTokenSession> findActiveByUserId(String userId) {
        RedisCommands<String, String> commands = connection.sync();
        String tokenId = commands.get(activeUserKey(userId));
        if (tokenId == null || tokenId.isBlank()) {
            return Optional.empty();
        }
        return findByTokenId(tokenId);
    }

    @Override
    public void revoke(String tokenId, String reason) {
        RedisCommands<String, String> commands = connection.sync();
        Optional<RefreshTokenSession> existing = findByTokenId(tokenId);
        if (existing.isEmpty()) {
            return;
        }

        RefreshTokenSession session = existing.get();
        RefreshTokenSession revoked = new RefreshTokenSession(
            session.tokenId(),
            session.userId(),
            session.sessionId(),
            session.expiresAt(),
            session.issuedAt(),
            Instant.now(),
            reason
        );

            String sessionKey = sessionKey(tokenId);
            commands.hset(sessionKey, toHash(revoked));
        commands.expire(sessionKey, ttlSeconds(revoked.expiresAt()));

        String activeKey = activeUserKey(revoked.userId());
        String activeTokenId = commands.get(activeKey);
        if (tokenId.equals(activeTokenId)) {
            commands.del(activeKey);
        }
    }

    @Override
    public void revokeByUserId(String userId, String reason) {
        findActiveByUserId(userId).ifPresent(session -> revoke(session.tokenId(), reason));
    }

      private Map<String, String> toHash(RefreshTokenSession session) {
        Map<String, String> values = new HashMap<>();
        values.put("tokenId", session.tokenId());
        values.put("userId", session.userId());
        values.put("sessionId", session.sessionId());
        values.put("expiresAt", session.expiresAt().toString());
        values.put("issuedAt", session.issuedAt().toString());
        values.put("revokedAt", session.revokedAt() == null ? "" : session.revokedAt().toString());
        values.put("revokedReason", session.revokedReason() == null ? "" : session.revokedReason());
        return values;
      }

    private RefreshTokenSession fromHash(Map<String, String> values) {
        return new RefreshTokenSession(
            values.get("tokenId"),
            values.get("userId"),
            values.get("sessionId"),
            Instant.parse(values.get("expiresAt")),
            Instant.parse(values.get("issuedAt")),
            parseInstant(values.get("revokedAt")),
            blankToNull(values.get("revokedReason"))
        );
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Instant.parse(value);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String sessionKey(String tokenId) {
        return properties.getKeyPrefix() + ":" + SESSION_PREFIX + tokenId;
    }

    private String activeUserKey(String userId) {
        return properties.getKeyPrefix() + ":" + ACTIVE_USER_PREFIX + userId;
    }

    private long ttlSeconds(Instant expiresAt) {
        long seconds = Duration.between(Instant.now(), expiresAt).getSeconds();
        return Math.max(1L, seconds);
    }
}

