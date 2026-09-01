package com.veterinaria.consentidos.features.auth.domain.entity;

/**
 * Local authenticated user model.
 *
 * <p>For this first iteration, the canonical user identifier is Google sub.
 */
public record AuthUser(
    String userId,
    String googleSub,
    String email,
    String displayName,
    boolean active
) {
}

