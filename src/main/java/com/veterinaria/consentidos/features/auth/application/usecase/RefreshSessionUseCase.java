package com.veterinaria.consentidos.features.auth.application.usecase;

import com.veterinaria.consentidos.features.auth.application.command.RefreshSessionCommand;
import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;
import com.veterinaria.consentidos.features.auth.domain.entity.RefreshTokenSession;
import com.veterinaria.consentidos.features.auth.domain.entity.TokenPair;
import com.veterinaria.consentidos.features.auth.domain.repository.AuthUserRepository;
import com.veterinaria.consentidos.features.auth.domain.repository.JwtTokenService;
import com.veterinaria.consentidos.features.auth.domain.repository.RefreshTokenSessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshSessionUseCase {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final AuthUserRepository authUserRepository;

    public RefreshSessionUseCase(
        JwtTokenService jwtTokenService,
        RefreshTokenSessionRepository refreshTokenSessionRepository,
        AuthUserRepository authUserRepository
    ) {
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
        this.authUserRepository = authUserRepository;
    }

    public TokenPair execute(RefreshSessionCommand command) {
        JwtTokenService.ParsedRefreshToken parsed = jwtTokenService.parseRefreshToken(command.refreshToken());
        RefreshTokenSession savedSession = refreshTokenSessionRepository.findByTokenId(parsed.tokenId())
            .orElseThrow(() -> new SecurityException("Refresh token not found"));

        if (savedSession.isRevoked()) {
            throw new SecurityException("Refresh token already revoked");
        }

        if (savedSession.expiresAt().isBefore(Instant.now())) {
            refreshTokenSessionRepository.revoke(savedSession.tokenId(), "expired");
            throw new SecurityException("Refresh token expired");
        }

        AuthUser user = authUserRepository.findActiveByUserId(parsed.userId())
            .orElseThrow(() -> new SecurityException("User is not active"));

        refreshTokenSessionRepository.revoke(savedSession.tokenId(), "rotated");

        String newRefreshTokenId = UUID.randomUUID().toString();
        JwtTokenService.SignedToken access = jwtTokenService.createAccessToken(user, parsed.sessionId());
        JwtTokenService.SignedToken refresh = jwtTokenService.createRefreshToken(user, parsed.sessionId(), newRefreshTokenId);

        refreshTokenSessionRepository.save(new RefreshTokenSession(
            newRefreshTokenId,
            user.userId(),
            parsed.sessionId(),
            refresh.expiresAt(),
            Instant.now(),
            null,
            null
        ));

        return new TokenPair(
            access.token(),
            refresh.token(),
            access.expiresAt(),
            refresh.expiresAt(),
            "Bearer",
            parsed.sessionId()
        );
    }
}

