package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository interface for Person entity.
 */
@Repository
public interface PersonJpaRepository extends JpaRepository<Person, Long> {

    /**
     * Finds a person by document number (any state — active or historical).
     */
    Optional<Person> findByDocuments_DocumentNumber(String documentNumber);

    /**
     * Finds persons by city (case-insensitive).
     */
    List<Person> findByCityIgnoreCase(String city);

    /**
     * Finds persons by document type code (case-insensitive), active documents only.
     */
    List<Person> findByDocuments_DocumentType_CodeIgnoreCaseAndDocuments_IsActiveTrue(String code);

    /**
     * Checks if any person already holds the given document number.
     */
    boolean existsByDocuments_DocumentNumber(String documentNumber);
}