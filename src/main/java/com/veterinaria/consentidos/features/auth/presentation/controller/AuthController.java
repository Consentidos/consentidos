package com.veterinaria.consentidos.features.auth.presentation.controller;

import com.veterinaria.consentidos.features.auth.application.command.LoginWithGoogleCommand;
import com.veterinaria.consentidos.features.auth.application.command.LogoutCommand;
import com.veterinaria.consentidos.features.auth.application.command.RefreshSessionCommand;
import com.veterinaria.consentidos.features.auth.application.usecase.LoginWithGoogleUseCase;
import com.veterinaria.consentidos.features.auth.application.usecase.LogoutUseCase;
import com.veterinaria.consentidos.features.auth.application.usecase.RefreshSessionUseCase;
import com.veterinaria.consentidos.features.auth.domain.entity.TokenPair;
import com.veterinaria.consentidos.features.auth.presentation.dto.request.LoginRequestDto;
import com.veterinaria.consentidos.features.auth.presentation.dto.request.LogoutRequestDto;
import com.veterinaria.consentidos.features.auth.presentation.dto.request.RefreshTokenRequestDto;
import com.veterinaria.consentidos.features.auth.presentation.dto.response.AuthTokenResponseDto;
import com.veterinaria.consentidos.features.auth.presentation.dto.response.LogoutResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final LoginWithGoogleUseCase loginWithGoogleUseCase;
	private final RefreshSessionUseCase refreshSessionUseCase;
	private final LogoutUseCase logoutUseCase;

	public AuthController(
		LoginWithGoogleUseCase loginWithGoogleUseCase,
		RefreshSessionUseCase refreshSessionUseCase,
		LogoutUseCase logoutUseCase
	) {
		this.loginWithGoogleUseCase = loginWithGoogleUseCase;
		this.refreshSessionUseCase = refreshSessionUseCase;
		this.logoutUseCase = logoutUseCase;
	}

	@PostMapping("/login")
	public AuthTokenResponseDto login(@Valid @RequestBody LoginRequestDto request) {
		TokenPair tokenPair = loginWithGoogleUseCase.execute(new LoginWithGoogleCommand(request.idToken()));
		return toResponse(tokenPair);
	}

	@PostMapping("/refresh")
	public AuthTokenResponseDto refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
		TokenPair tokenPair = refreshSessionUseCase.execute(new RefreshSessionCommand(request.refreshToken()));
		return toResponse(tokenPair);
	}

	@PostMapping("/logout")
	public LogoutResponseDto logout(@Valid @RequestBody LogoutRequestDto request) {
		logoutUseCase.execute(new LogoutCommand(request.refreshToken()));
		return new LogoutResponseDto("Session closed successfully");
	}

	// TODO: Future endpoint for RBAC retrieval from authenticated user context.
	// @GetMapping("/me")
	// public MeResponseDto me(Authentication authentication) {
	//     return ...; // Resolve user roles/permissions with caching strategy (e.g., Redis).
	// }

	private AuthTokenResponseDto toResponse(TokenPair tokenPair) {
		return new AuthTokenResponseDto(
			tokenPair.tokenType(),
			tokenPair.accessToken(),
			tokenPair.refreshToken(),
			tokenPair.accessTokenExpiresAt(),
			tokenPair.refreshTokenExpiresAt(),
			tokenPair.sessionId()
		);
	}
}
