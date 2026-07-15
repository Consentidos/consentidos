package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import com.veterinaria.consentidos.features.documentIdentifier.domain.repository.DocumentIdentifierRepository;
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
 */
@Service
public class UpdatePersonUseCase {

    private final PersonRepository personRepository;
    private final DocumentIdentifierRepository documentIdentifierRepository;

    @Autowired
    public UpdatePersonUseCase(PersonRepository personRepository, DocumentIdentifierRepository documentIdentifierRepository) {
        this.personRepository = personRepository;
        this.documentIdentifierRepository = documentIdentifierRepository;
    }

    @Transactional
    public PersonDto execute(UpdatePersonCommand command) {
        Person existingPerson = personRepository.findById(command.getId())
                .orElseThrow(() -> new PersonNotFoundException(command.getId()));

        String currentDocumentNumber = existingPerson.getDocumentNumber();
        String newDocumentNumber = command.getDocumentNumber();

        boolean isDocumentChanged = !Objects.equals(currentDocumentNumber, newDocumentNumber);
        if (isDocumentChanged && personRepository.existsByDocument(newDocumentNumber)) {
            throw new PersonAlreadyExistsException(newDocumentNumber);
        }

        DocumentIdentifier documentIdentifier = documentIdentifierRepository
                .findById(command.getDocumentIdentifierId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document identifier not found: " + command.getDocumentIdentifierId()));

        existingPerson.setSex(command.getSex());
        existingPerson.setFirstName(command.getFirstName());
        existingPerson.setLastName(command.getLastName());
        existingPerson.setCity(command.getCity());
        existingPerson.setDocumentNumber(newDocumentNumber);
        existingPerson.setDocumentIdentifier(documentIdentifier);

        return PersonDto.fromEntity(personRepository.save(existingPerson));
    }
}