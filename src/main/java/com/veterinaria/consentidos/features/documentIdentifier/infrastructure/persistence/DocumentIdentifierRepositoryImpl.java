package com.veterinaria.consentidos.features.documentIdentifier.infrastructure.persistence;

import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import com.veterinaria.consentidos.features.documentIdentifier.domain.repository.DocumentIdentifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for DocumentIdentifierRepository.
 */
@Component
public class DocumentIdentifierRepositoryImpl implements DocumentIdentifierRepository {

    private final DocumentIdentifierJpaRepository jpaRepository;

    @Autowired
    public DocumentIdentifierRepositoryImpl(DocumentIdentifierJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<DocumentIdentifier> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DocumentIdentifier> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public DocumentIdentifier save(DocumentIdentifier documentIdentifier) {
        return jpaRepository.save(documentIdentifier);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
