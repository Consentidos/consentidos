package com.veterinaria.consentidos.features.auth.infrastructure.persistence.repository;

import com.veterinaria.consentidos.features.auth.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserJpaRepository extends JpaRepository<AuthUserJpaEntity, UUID> {
    Optional<AuthUserJpaEntity> findByGoogleSubAndActiveTrue(String googleSub);

    Optional<AuthUserJpaEntity> findByIdAndActiveTrue(UUID id);
}

