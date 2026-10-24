package com.veterinaria.consentidos.features.auth.application.usecase;

import com.veterinaria.consentidos.features.auth.application.command.LoginWithGoogleCommand;
import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;
import com.veterinaria.consentidos.features.auth.domain.entity.GoogleIdentity;
import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptPolicy;
import com.veterinaria.consentidos.features.auth.domain.entity.RefreshTokenSession;
import com.veterinaria.consentidos.features.auth.domain.entity.TokenPair;
import com.veterinaria.consentidos.features.auth.domain.repository.AuthUserRepository;
import com.veterinaria.consentidos.features.auth.domain.repository.GoogleIdentityVerifier;
import com.veterinaria.consentidos.features.auth.domain.repository.JwtTokenService;
import com.veterinaria.consentidos.features.auth.domain.repository.LoginAttemptRepository;
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
    private final LoginAttemptRepository loginAttemptRepository;
    private final LoginAttemptPolicy loginAttemptPolicy;

    public LoginWithGoogleUseCase(
        GoogleIdentityVerifier googleIdentityVerifier,
        AuthUserRepository authUserRepository,
        JwtTokenService jwtTokenService,
        RefreshTokenSessionRepository refreshTokenSessionRepository,
        LoginAttemptRepository loginAttemptRepository,
        LoginAttemptPolicy loginAttemptPolicy
    ) {
        this.googleIdentityVerifier = googleIdentityVerifier;
        this.authUserRepository = authUserRepository;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.loginAttemptPolicy = loginAttemptPolicy;
    }

    public TokenPair execute(LoginWithGoogleCommand command) {
        GoogleIdentity identity;
        try {
            identity = googleIdentityVerifier.verifyIdToken(command.idToken());
        } catch (SecurityException ex) {
            registerFailure("unknown", command.deviceId(), command.clientIp(), command.userAgent(), "unknown");
            throw ex;
        }

        String attemptKey = loginAttemptKey(identity.sub(), command.deviceId());
        loginAttemptRepository.findByKey(attemptKey)
            .filter(state -> state.isBlocked(Instant.now()))
            .ifPresent(state -> {
                throw new IllegalArgumentException("Login temporarily blocked");
            });

        AuthUser user = authUserRepository.findActiveByGoogleSub(identity.sub()).orElse(null);
        if (user == null) {
            registerFailure(attemptKey, command.deviceId(), command.clientIp(), command.userAgent(), identity.sub());
            throw new SecurityException("Invalid login credentials");
        }

        refreshTokenSessionRepository.revokeByUserId(user.userId(), "single_session_replaced");

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

        loginAttemptRepository.clear(attemptKey);

        return new TokenPair(
            access.token(),
            refresh.token(),
            access.expiresAt(),
            refresh.expiresAt(),
            "Bearer",
            sessionId
        );
    }

    private void registerFailure(
        String key,
        String deviceId,
        String clientIp,
        String userAgent,
        String userHint
    ) {
        loginAttemptRepository.registerFailure(
            key,
            userHint,
            deviceId,
            clientIp,
            userAgent,
            loginAttemptPolicy
        );
    }

    private String loginAttemptKey(String userId, String deviceId) {
        return userId + ":" + (deviceId == null || deviceId.isBlank() ? "unknown-device" : deviceId);
    }
}

