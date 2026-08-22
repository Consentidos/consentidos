package com.veterinaria.consentidos.features.documenttype.infrastructure.persistence;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for DocumentType.
 */
@Repository
public interface DocumentTypeJpaRepository extends JpaRepository<DocumentType, Long> {
}
