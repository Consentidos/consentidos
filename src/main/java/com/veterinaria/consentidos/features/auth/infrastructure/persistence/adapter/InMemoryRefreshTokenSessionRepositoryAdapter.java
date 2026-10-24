package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.RefreshTokenSession;
import com.veterinaria.consentidos.features.auth.domain.repository.RefreshTokenSessionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Profile("local & !external-storage")
public class InMemoryRefreshTokenSessionRepositoryAdapter implements RefreshTokenSessionRepository {

    private final ConcurrentMap<String, RefreshTokenSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> activeTokenByUserId = new ConcurrentHashMap<>();

    @Override
    public void save(RefreshTokenSession session) {
        sessions.put(session.tokenId(), session);
        activeTokenByUserId.put(session.userId(), session.tokenId());
    }

    @Override
    public Optional<RefreshTokenSession> findByTokenId(String tokenId) {
        return Optional.ofNullable(sessions.get(tokenId));
    }

    @Override
    public Optional<RefreshTokenSession> findActiveByUserId(String userId) {
        String tokenId = activeTokenByUserId.get(userId);
        if (tokenId == null) {
            return Optional.empty();
        }
        return findByTokenId(tokenId);
    }

    @Override
    public void revoke(String tokenId, String reason) {
        sessions.computeIfPresent(tokenId, (key, old) -> new RefreshTokenSession(
            old.tokenId(),
            old.userId(),
            old.sessionId(),
            old.expiresAt(),
            old.issuedAt(),
            Instant.now(),
            reason
        ));

        RefreshTokenSession session = sessions.get(tokenId);
        if (session != null && tokenId.equals(activeTokenByUserId.get(session.userId()))) {
            activeTokenByUserId.remove(session.userId());
        }
    }

    @Override
    public void revokeByUserId(String userId, String reason) {
        String tokenId = activeTokenByUserId.get(userId);
        if (tokenId != null) {
            revoke(tokenId, reason);
        }
    }
}

