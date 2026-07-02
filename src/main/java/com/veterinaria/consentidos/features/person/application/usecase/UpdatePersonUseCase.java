package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.application.command.UpdatePersonCommand;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * Use case for updating an existing person.
 * This class contains the business logic for person updates,
 * including validation and data persistence.
 */
@Service
public class UpdatePersonUseCase {

    private final PersonRepository personRepository;

    @Autowired
    public UpdatePersonUseCase(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Updates an existing person based on the provided command.
     *
     * @param command the command containing updated person data
     * @return PersonDto representing the updated person
     * @throws IllegalArgumentException if the person is not found or document already exists
     */
    @Transactional
    public PersonDto execute(UpdatePersonCommand command) {
        // Find the existing person
        Optional<Person> existingPersonOpt = personRepository.findById(command.getId());
        if (existingPersonOpt.isEmpty()) {
            throw new IllegalArgumentException("Person with ID " + command.getId() + " not found");
        }

        Person existingPerson = existingPersonOpt.get();

        // Check if document is being changed and if new document already exists
        if (!existingPerson.getDocument().equals(command.getDocument())) {
            if (personRepository.existsByDocument(command.getDocument())) {
                throw new IllegalArgumentException("A person with document " + command.getDocument() + " already exists");
            }
        }

        // Update the person data
        existingPerson.setSex(command.getSex());
        existingPerson.setFirstName(command.getFirstName());
        existingPerson.setLastName(command.getLastName());
        existingPerson.setCity(command.getCity());
        existingPerson.setDocument(command.getDocument());
        existingPerson.setDocumentType(command.getDocumentType());

        // Save the updated person
        Person updatedPerson = personRepository.save(existingPerson);

        // Convert to DTO and return
        return PersonDto.fromEntity(updatedPerson);
    }
}