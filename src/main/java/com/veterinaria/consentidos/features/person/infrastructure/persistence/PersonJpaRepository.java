package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * @param document the person's document number
     * @return an Optional containing the person if found, empty otherwise
     */
    Optional<Person> findByDocument(String document);

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
    List<Person> findByDocumentTypeIgnoreCase(String documentType);

    /**
     * Checks if a person exists with the given document number.
     *
     * @param document the document number to check
     * @return true if a person exists with this document, false otherwise
     */
    boolean existsByDocument(String document);

    /**
     * Finds persons by sex.
     *
     * @param sex the sex to search for (M or F)
     * @return a list of persons with the specified sex
     */
    List<Person> findBySex(String sex);

    /**
     * Finds persons by first name containing the given text (case-insensitive).
     *
     * @param firstName the first name pattern to search for
     * @return a list of persons whose first name contains the specified text
     */
    List<Person> findByFirstNameContainingIgnoreCase(String firstName);

    /**
     * Finds persons by last name containing the given text (case-insensitive).
     *
     * @param lastName the last name pattern to search for
     * @return a list of persons whose last name contains the specified text
     */
    List<Person> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Custom query to find persons by full name (first name + last name).
     *
     * @param fullName the full name to search for
     * @return a list of persons matching the full name
     */
    @Query("SELECT p FROM Person p WHERE CONCAT(p.firstName, ' ', p.lastName) LIKE %:fullName%")
    List<Person> findByFullNameContaining(@Param("fullName") String fullName);

    /**
     * Counts persons by city.
     *
     * @param city the city to count persons for
     * @return the number of persons in the specified city
     */
    long countByCity(String city);

    /**
     * Counts persons by document type.
     *
     * @param documentType the document type to count persons for
     * @return the number of persons with the specified document type
     */
    long countByDocumentType(String documentType);
}