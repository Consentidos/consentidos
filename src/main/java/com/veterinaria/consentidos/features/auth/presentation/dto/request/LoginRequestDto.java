package com.veterinaria.consentidos.features.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "idToken is required")
    String idToken
) {
}

