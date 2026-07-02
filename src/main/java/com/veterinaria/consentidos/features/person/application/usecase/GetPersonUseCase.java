package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Use case for retrieving person information.
 * This class contains the business logic for person retrieval operations.
 */
@Service
@Transactional(readOnly = true)
public class GetPersonUseCase {

    private final PersonRepository personRepository;

    @Autowired
    public GetPersonUseCase(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Retrieves a person by their ID.
     *
     * @param id the person's ID
     * @return PersonDto if found, null otherwise
     */
    public PersonDto getById(Long id) {
        Optional<Person> person = personRepository.findById(id);
        return person.map(PersonDto::fromEntity).orElse(null);
    }

    /**
     * Retrieves a person by their document number.
     *
     * @param document the person's document number
     * @return PersonDto if found, null otherwise
     */
    public PersonDto getByDocument(String document) {
        Optional<Person> person = personRepository.findByDocument(document);
        return person.map(PersonDto::fromEntity).orElse(null);
    }

    /**
     * Retrieves all persons in the system.
     *
     * @return List of PersonDto representing all persons
     */
    public List<PersonDto> getAll() {
        return personRepository.findAll()
                .stream()
                .map(PersonDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves persons by city.
     *
     * @param city the city to search for
     * @return List of PersonDto representing persons in the specified city
     */
    public List<PersonDto> getByCity(String city) {
        return personRepository.findByCity(city)
                .stream()
                .map(PersonDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves persons by document type.
     *
     * @param documentType the document type to search for
     * @return List of PersonDto representing persons with the specified document type
     */
    public List<PersonDto> getByDocumentType(String documentType) {
        return personRepository.findByDocumentType(documentType)
                .stream()
                .map(PersonDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a person exists with the given document number.
     *
     * @param document the document number to check
     * @return true if a person exists with this document, false otherwise
     */
    public boolean existsByDocument(String document) {
        return personRepository.existsByDocument(document);
    }

    /**
     * Gets the total count of persons in the system.
     *
     * @return the total number of persons
     */
    public long getTotalCount() {
        return personRepository.count();
    }
}