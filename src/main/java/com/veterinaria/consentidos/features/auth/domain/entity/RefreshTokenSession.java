package com.veterinaria.consentidos.features.auth.domain.entity;

import java.time.Instant;

public record RefreshTokenSession(
    String tokenId,
    String userId,
    String sessionId,
    Instant expiresAt,
    Instant issuedAt,
    Instant revokedAt,
    String revokedReason
) {
    public boolean isRevoked() {
        return revokedAt != null;
    }
}

