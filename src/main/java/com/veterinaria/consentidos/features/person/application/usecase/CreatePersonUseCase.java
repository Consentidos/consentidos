package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import com.veterinaria.consentidos.features.documenttype.domain.repository.DocumentTypeRepository;
import com.veterinaria.consentidos.features.person.application.command.CreatePersonCommand;
import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.entity.PersonDocument;
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
    private final DocumentTypeRepository documentTypeRepository;

    @Autowired
    public CreatePersonUseCase(PersonRepository personRepository, DocumentTypeRepository documentTypeRepository) {
        this.personRepository = personRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    @Transactional
    public PersonDto execute(CreatePersonCommand command) {
        if (personRepository.existsByDocument(command.getDocumentNumber())) {
            throw new PersonAlreadyExistsException(command.getDocumentNumber());
        }

        DocumentType documentType = documentTypeRepository
                .findById(command.getDocumentTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document type not found: " + command.getDocumentTypeId()));

        Person person = new Person();
        person.setSex(command.getSex());
        person.setBirthDate(command.getBirthDate());
        person.setFirstName(command.getFirstName());
        person.setLastName(command.getLastName());
        person.setCity(command.getCity());

        PersonDocument document = new PersonDocument(person, command.getDocumentNumber(), documentType);
        person.addDocument(document);

        return PersonDto.fromEntity(personRepository.save(person));
    }
}