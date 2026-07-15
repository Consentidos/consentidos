package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository interface for Person entity.
 * This interface extends JpaRepository to provide basic CRUD operations
 * and defines custom query methods for Person-specific operations.
 */
@Repository
public interface PersonJpaRepository extends JpaRepository<Person, Long> {

    /**
     * Finds a person by their document number.
     *
     * @param documentNumber the person's document number
     * @return an Optional containing the person if found, empty otherwise
     */
    Optional<Person> findByIdentification_DocumentNumber(String documentNumber);

    /**
     * Finds persons by their city (case-insensitive).
     *
     * @param city the city to search for
     * @return a list of persons living in the specified city
     */
    List<Person> findByCityIgnoreCase(String city);

    /**
     * Finds persons by their document type (case-insensitive).
     *
     * @param documentType the document type to search for
     * @return a list of persons with the specified document type
     */
    List<Person> findByIdentification_DocumentIdentifier_DocumentTypeIgnoreCase(String documentType);

    /**
     * Checks if a person exists with the given document number.
     *
     * @param documentNumber the document number to check
     * @return true if a person exists with this document, false otherwise
     */
    boolean existsByIdentification_DocumentNumber(String documentNumber);
}