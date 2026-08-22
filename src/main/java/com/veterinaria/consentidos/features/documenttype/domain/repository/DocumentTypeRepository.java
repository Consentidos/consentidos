package com.veterinaria.consentidos.features.documenttype.domain.repository;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for DocumentType operations.
 */
public interface DocumentTypeRepository {

    Optional<DocumentType> findById(Long id);

    List<DocumentType> findAll();

    DocumentType save(DocumentType documentType);

    boolean existsById(Long id);
}
