package com.veterinaria.consentidos.features.auth.domain.repository;

import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;

import java.time.Instant;

public interface JwtTokenService {
    SignedToken createAccessToken(AuthUser user, String sessionId);

    SignedToken createRefreshToken(AuthUser user, String sessionId, String tokenId);

    ParsedRefreshToken parseRefreshToken(String refreshToken);

    record SignedToken(String token, Instant expiresAt, String tokenId) {
    }

    record ParsedRefreshToken(String userId, String sessionId, String tokenId, Instant expiresAt) {
    }
}

