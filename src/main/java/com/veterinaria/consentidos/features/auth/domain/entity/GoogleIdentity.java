package com.veterinaria.consentidos.features.auth.domain.entity;

public record GoogleIdentity(
    String sub,
    String email,
    boolean emailVerified,
    String name,
    String picture,
    String issuer,
    String audience
) {
}

