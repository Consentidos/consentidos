package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.domain.exception.PersonNotFoundException;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
     * @return PersonDto if found
     * @throws PersonNotFoundException if the person does not exist
     */
    public PersonDto getById(Long id) {
        return personRepository.findById(id)
                .map(PersonDto::fromEntity)
                .orElseThrow(() -> new PersonNotFoundException(id));
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
                .toList();
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
                .toList();
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
                .toList();
    }
}