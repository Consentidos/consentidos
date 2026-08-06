package com.veterinaria.consentidos.features.documentidentifier.infrastructure.persistence;

import com.veterinaria.consentidos.features.documentidentifier.domain.entity.DocumentIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for DocumentIdentifier entity.
 */
@Repository
public interface DocumentIdentifierJpaRepository extends JpaRepository<DocumentIdentifier, Long> {
}
