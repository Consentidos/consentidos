package com.veterinaria.consentidos.features.documentidentifier.domain.repository;

import com.veterinaria.consentidos.features.documentidentifier.domain.entity.DocumentIdentifier;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for DocumentIdentifier operations.
 */
public interface DocumentIdentifierRepository {

    Optional<DocumentIdentifier> findById(Long id);

    List<DocumentIdentifier> findAll();

    DocumentIdentifier save(DocumentIdentifier documentIdentifier);

    boolean existsById(Long id);
}
