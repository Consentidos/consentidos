package com.veterinaria.consentidos.features.auth.domain.repository;

import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;

import java.util.Optional;

public interface AuthUserRepository {
    Optional<AuthUser> findActiveByGoogleSub(String googleSub);

    Optional<AuthUser> findActiveByUserId(String userId);
}

