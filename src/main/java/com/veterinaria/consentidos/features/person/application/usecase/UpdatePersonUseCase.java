package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import com.veterinaria.consentidos.features.documenttype.domain.repository.DocumentTypeRepository;
import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.application.command.UpdatePersonCommand;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.entity.PersonDocument;
import com.veterinaria.consentidos.features.person.domain.exception.PersonAlreadyExistsException;
import com.veterinaria.consentidos.features.person.domain.exception.PersonNotFoundException;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

/**
 * Use case for updating an existing person.
 * When the document number or type changes, a new PersonDocument is inserted
 * and the DB trigger automatically deactivates the previous one.
 */
@Service
public class UpdatePersonUseCase {

    private final PersonRepository personRepository;
    private final DocumentTypeRepository documentTypeRepository;

    @Autowired
    public UpdatePersonUseCase(PersonRepository personRepository, DocumentTypeRepository documentTypeRepository) {
        this.personRepository = personRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    @Transactional
    public PersonDto execute(UpdatePersonCommand command) {
        Person existingPerson = personRepository.findById(command.getId())
                .orElseThrow(() -> new PersonNotFoundException(command.getId()));

        String currentDocumentNumber = existingPerson.getDocumentNumber();
        String newDocumentNumber = command.getDocumentNumber();

        boolean documentNumberChanged = !Objects.equals(currentDocumentNumber, newDocumentNumber);
        if (documentNumberChanged && personRepository.existsByDocument(newDocumentNumber)) {
            throw new PersonAlreadyExistsException(newDocumentNumber);
        }

        DocumentType documentType = documentTypeRepository
                .findById(command.getDocumentTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document type not found: " + command.getDocumentTypeId()));

        existingPerson.setSex(command.getSex());
        existingPerson.setFirstName(command.getFirstName());
        existingPerson.setLastName(command.getLastName());
        existingPerson.setCity(command.getCity());

        Long currentDocumentTypeId = existingPerson.getDocumentType() != null
                ? existingPerson.getDocumentType().getId() : null;
        boolean documentChanged = documentNumberChanged
                || !Objects.equals(currentDocumentTypeId, command.getDocumentTypeId());

        if (documentChanged) {
            PersonDocument newDoc = new PersonDocument(existingPerson, newDocumentNumber, documentType);
            existingPerson.addDocument(newDoc);
        }

        return PersonDto.fromEntity(personRepository.save(existingPerson));
    }
}