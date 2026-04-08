package com.veterinaria.consentidos.features.person.domain.repository;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Person entity operations.
 * This interface defines the contract for data access operations on Person entities.
 * It follows the Domain-Driven Design (DDD) pattern by being part of the domain layer.
 */
public interface PersonRepository {

    /**
     * Saves a person to the data store.
     *
     * @param person the person to save
     * @return the saved person with generated ID
     */
    Person save(Person person);

    /**
     * Finds a person by their unique identifier.
     *
     * @param id the person's ID
     * @return an Optional containing the person if found, empty otherwise
     */
    Optional<Person> findById(Long id);

    /**
     * Finds a person by their document number.
     *
     * @param document the person's document number
     * @return an Optional containing the person if found, empty otherwise
     */
    Optional<Person> findByDocument(String document);

    /**
     * Finds all persons in the system.
     *
     * @return a list of all persons
     */
    List<Person> findAll();

    /**
     * Finds persons by their city.
     *
     * @param city the city to search for
     * @return a list of persons living in the specified city
     */
    List<Person> findByCity(String city);

    /**
     * Finds persons by their document type.
     *
     * @param documentType the document type to search for
     * @return a list of persons with the specified document type
     */
    List<Person> findByDocumentType(String documentType);

    /**
     * Checks if a person exists with the given document number.
     *
     * @param document the document number to check
     * @return true if a person exists with this document, false otherwise
     */
    boolean existsByDocument(String document);

    /**
     * Deletes a person by their ID.
     *
     * @param id the ID of the person to delete
     */
    void deleteById(Long id);

    /**
     * Counts the total number of persons in the system.
     *
     * @return the total count of persons
     */
    long count();
}