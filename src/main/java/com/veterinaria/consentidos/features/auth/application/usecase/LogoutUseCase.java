package com.veterinaria.consentidos.features.auth.application.usecase;

import com.veterinaria.consentidos.features.auth.application.command.LogoutCommand;
import com.veterinaria.consentidos.features.auth.domain.repository.JwtTokenService;
import com.veterinaria.consentidos.features.auth.domain.repository.RefreshTokenSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;

    public LogoutUseCase(
        JwtTokenService jwtTokenService,
        RefreshTokenSessionRepository refreshTokenSessionRepository
    ) {
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
    }

    public void execute(LogoutCommand command) {
        JwtTokenService.ParsedRefreshToken parsed = jwtTokenService.parseRefreshToken(command.refreshToken());
        refreshTokenSessionRepository.revoke(parsed.tokenId(), "logout");
    }
}

