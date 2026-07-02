package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.person.application.command.CreatePersonCommand;
import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for creating a new person.
 * This class contains the business logic for person creation,
 * including validation and data persistence.
 */
@Service
public class CreatePersonUseCase {

    private final PersonRepository personRepository;

    @Autowired
    public CreatePersonUseCase(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Creates a new person based on the provided command.
     *
     * @param command the command containing person data
     * @return PersonDto representing the created person
     * @throws IllegalArgumentException if a person with the same document already exists
     */
    @Transactional
    public PersonDto execute(CreatePersonCommand command) {
        // Validate that document doesn't already exist
        if (personRepository.existsByDocument(command.getDocument())) {
            throw new IllegalArgumentException("A person with document " + command.getDocument() + " already exists");
        }

        // Create and save the person
        Person person = new Person(
                command.getSex(),
                command.getBirthDate(),
                command.getFirstName(),
                command.getLastName(),
                command.getDocument(),
                command.getDocumentType(),
                command.getCity()
        );

        Person savedPerson = personRepository.save(person);

        // Convert to DTO and return
        return PersonDto.fromEntity(savedPerson);
    }
}