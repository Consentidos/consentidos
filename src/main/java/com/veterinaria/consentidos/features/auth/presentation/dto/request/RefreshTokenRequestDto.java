package com.veterinaria.consentidos.features.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
    @NotBlank(message = "refreshToken is required")
    String refreshToken
) {
}

