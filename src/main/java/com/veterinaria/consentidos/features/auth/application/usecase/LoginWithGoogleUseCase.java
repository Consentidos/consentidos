package com.veterinaria.consentidos.features.auth.application.usecase;

import com.veterinaria.consentidos.features.auth.application.command.LoginWithGoogleCommand;
import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;
import com.veterinaria.consentidos.features.auth.domain.entity.GoogleIdentity;
import com.veterinaria.consentidos.features.auth.domain.entity.RefreshTokenSession;
import com.veterinaria.consentidos.features.auth.domain.entity.TokenPair;
import com.veterinaria.consentidos.features.auth.domain.repository.AuthUserRepository;
import com.veterinaria.consentidos.features.auth.domain.repository.GoogleIdentityVerifier;
import com.veterinaria.consentidos.features.auth.domain.repository.JwtTokenService;
import com.veterinaria.consentidos.features.auth.domain.repository.RefreshTokenSessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoginWithGoogleUseCase {

    private final GoogleIdentityVerifier googleIdentityVerifier;
    private final AuthUserRepository authUserRepository;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;

    public LoginWithGoogleUseCase(
        GoogleIdentityVerifier googleIdentityVerifier,
        AuthUserRepository authUserRepository,
        JwtTokenService jwtTokenService,
        RefreshTokenSessionRepository refreshTokenSessionRepository
    ) {
        this.googleIdentityVerifier = googleIdentityVerifier;
        this.authUserRepository = authUserRepository;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
    }

    public TokenPair execute(LoginWithGoogleCommand command) {
        GoogleIdentity identity = googleIdentityVerifier.verifyIdToken(command.idToken());
        AuthUser user = authUserRepository.findActiveByGoogleSub(identity.sub())
            .orElseThrow(() -> new SecurityException("User is not pre-registered or is inactive"));

        String sessionId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();

        JwtTokenService.SignedToken access = jwtTokenService.createAccessToken(user, sessionId);
        JwtTokenService.SignedToken refresh = jwtTokenService.createRefreshToken(user, sessionId, refreshTokenId);

        refreshTokenSessionRepository.save(new RefreshTokenSession(
            refreshTokenId,
            user.userId(),
            sessionId,
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
            sessionId
        );
    }
}

