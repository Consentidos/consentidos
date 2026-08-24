package com.veterinaria.consentidos.features.auth.domain.repository;

import com.veterinaria.consentidos.features.auth.domain.entity.RefreshTokenSession;

import java.util.Optional;

public interface RefreshTokenSessionRepository {
    void save(RefreshTokenSession session);

    Optional<RefreshTokenSession> findByTokenId(String tokenId);

    void revoke(String tokenId, String reason);
}

