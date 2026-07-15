package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.application.command.UpdatePersonCommand;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.exception.PersonAlreadyExistsException;
import com.veterinaria.consentidos.features.person.domain.exception.PersonNotFoundException;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

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
        Person existingPerson = personRepository.findById(command.getId())
                .orElseThrow(() -> new PersonNotFoundException(command.getId()));

        String currentDocument = existingPerson.getDocument();
        String newDocument = command.getDocument();

        boolean isDocumentChanged = !Objects.equals(currentDocument, newDocument);
        if (isDocumentChanged && personRepository.existsByDocument(newDocument)) {
            throw new PersonAlreadyExistsException(newDocument);
        }

        existingPerson.setSex(command.getSex());
        existingPerson.setFirstName(command.getFirstName());
        existingPerson.setLastName(command.getLastName());
        existingPerson.setCity(command.getCity());
        existingPerson.setDocument(newDocument);
        existingPerson.setDocumentType(command.getDocumentType());

        return PersonDto.fromEntity(personRepository.save(existingPerson));
    }
}