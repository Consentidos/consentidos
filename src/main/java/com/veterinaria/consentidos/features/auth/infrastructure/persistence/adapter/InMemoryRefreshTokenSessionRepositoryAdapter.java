package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.RefreshTokenSession;
import com.veterinaria.consentidos.features.auth.domain.repository.RefreshTokenSessionRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryRefreshTokenSessionRepositoryAdapter implements RefreshTokenSessionRepository {

    private final ConcurrentMap<String, RefreshTokenSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void save(RefreshTokenSession session) {
        sessions.put(session.tokenId(), session);
    }

    @Override
    public Optional<RefreshTokenSession> findByTokenId(String tokenId) {
        return Optional.ofNullable(sessions.get(tokenId));
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
    }
}

