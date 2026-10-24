package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;
import com.veterinaria.consentidos.features.auth.domain.repository.AuthUserRepository;
import com.veterinaria.consentidos.features.auth.infrastructure.persistence.repository.AuthUserJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Profile("!local | external-storage")
public class PostgresAuthUserRepositoryAdapter implements AuthUserRepository {

    private final AuthUserJpaRepository authUserJpaRepository;

    public PostgresAuthUserRepositoryAdapter(AuthUserJpaRepository authUserJpaRepository) {
        this.authUserJpaRepository = authUserJpaRepository;
    }

    @Override
    public Optional<AuthUser> findActiveByGoogleSub(String googleSub) {
        return authUserJpaRepository.findByGoogleSubAndActiveTrue(googleSub)
            .map(com.veterinaria.consentidos.features.auth.infrastructure.persistence.entity.AuthUserJpaEntity::toDomain);
    }

    @Override
    public Optional<AuthUser> findActiveByUserId(String userId) {
        try {
            UUID id = UUID.fromString(userId);
            return authUserJpaRepository.findByIdAndActiveTrue(id)
                .map(com.veterinaria.consentidos.features.auth.infrastructure.persistence.entity.AuthUserJpaEntity::toDomain);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}


