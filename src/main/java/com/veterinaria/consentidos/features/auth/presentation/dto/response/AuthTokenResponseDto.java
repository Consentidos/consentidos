package com.veterinaria.consentidos.features.auth.presentation.dto.response;

import java.time.Instant;

public record AuthTokenResponseDto(
    String tokenType,
    String accessToken,
    String refreshToken,
    Instant accessTokenExpiresAt,
    Instant refreshTokenExpiresAt,
    String sessionId
) {
}

