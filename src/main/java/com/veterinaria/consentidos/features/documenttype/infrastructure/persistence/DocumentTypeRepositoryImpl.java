package com.veterinaria.consentidos.features.documenttype.infrastructure.persistence;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import com.veterinaria.consentidos.features.documenttype.domain.repository.DocumentTypeRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for DocumentTypeRepository.
 */
@Repository
public class DocumentTypeRepositoryImpl implements DocumentTypeRepository {

    private final DocumentTypeJpaRepository jpaRepository;

    public DocumentTypeRepositoryImpl(DocumentTypeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<DocumentType> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DocumentType> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public DocumentType save(DocumentType documentType) {
        return jpaRepository.save(documentType);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
