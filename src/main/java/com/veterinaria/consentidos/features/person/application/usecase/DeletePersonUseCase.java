package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.person.domain.exception.PersonNotFoundException;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for deleting a person.
 * This class contains the business logic for person deletion.
 */
@Service
public class DeletePersonUseCase {

    private final PersonRepository personRepository;

    public DeletePersonUseCase(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Deletes a person by their ID.
     *
     * @param id the ID of the person to delete
     * @throws IllegalArgumentException if the person is not found
     */
    @Transactional
    public void execute(Long id) {
        if (personRepository.findById(id).isEmpty()) {
            throw new PersonNotFoundException(id);
        }

        personRepository.deleteById(id);
    }

    /**
     * Checks if a person can be deleted (exists in the system).
     *
     * @param id the ID of the person to check
     * @return true if the person exists and can be deleted, false otherwise
     */
    public boolean canDelete(Long id) {
        return personRepository.findById(id).isPresent();
    }
}