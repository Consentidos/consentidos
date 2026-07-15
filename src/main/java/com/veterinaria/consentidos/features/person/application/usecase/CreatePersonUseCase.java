package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import com.veterinaria.consentidos.features.documentIdentifier.domain.repository.DocumentIdentifierRepository;
import com.veterinaria.consentidos.features.person.application.command.CreatePersonCommand;
import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.exception.PersonAlreadyExistsException;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for creating a new person.
 */
@Service
public class CreatePersonUseCase {

    private final PersonRepository personRepository;
    private final DocumentIdentifierRepository documentIdentifierRepository;

    @Autowired
    public CreatePersonUseCase(PersonRepository personRepository, DocumentIdentifierRepository documentIdentifierRepository) {
        this.personRepository = personRepository;
        this.documentIdentifierRepository = documentIdentifierRepository;
    }

    @Transactional
    public PersonDto execute(CreatePersonCommand command) {
        if (personRepository.existsByDocument(command.getDocumentNumber())) {
            throw new PersonAlreadyExistsException(command.getDocumentNumber());
        }

        DocumentIdentifier documentIdentifier = documentIdentifierRepository
                .findById(command.getDocumentIdentifierId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document identifier not found: " + command.getDocumentIdentifierId()));

        Person person = new Person(
                command.getSex(),
                command.getBirthDate(),
                command.getFirstName(),
                command.getLastName(),
                command.getDocumentNumber(),
                documentIdentifier,
                command.getCity()
        );

        return PersonDto.fromEntity(personRepository.save(person));
    }
}