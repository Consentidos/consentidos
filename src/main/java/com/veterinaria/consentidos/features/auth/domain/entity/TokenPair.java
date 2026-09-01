package com.veterinaria.consentidos.features.auth.domain.entity;

import java.time.Instant;

public record TokenPair(
    String accessToken,
    String refreshToken,
    Instant accessTokenExpiresAt,
    Instant refreshTokenExpiresAt,
    String tokenType,
    String sessionId
) {
}

