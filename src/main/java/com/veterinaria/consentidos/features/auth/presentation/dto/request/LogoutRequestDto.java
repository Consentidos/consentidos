package com.veterinaria.consentidos.features.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDto(
    @NotBlank(message = "refreshToken is required")
    String refreshToken
) {
}

