package com.veterinaria.consentidos.features.person.presentation.controller;

import com.veterinaria.consentidos.features.person.application.command.CreatePersonCommand;
import com.veterinaria.consentidos.features.person.application.command.PersonDto;
import com.veterinaria.consentidos.features.person.application.command.UpdatePersonCommand;
import com.veterinaria.consentidos.features.person.application.usecase.CreatePersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.DeletePersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.GetPersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.UpdatePersonUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for Person management.
 * This controller provides HTTP endpoints for CRUD operations on Person entities.
 */
@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final CreatePersonUseCase createPersonUseCase;
    private final GetPersonUseCase getPersonUseCase;
    private final UpdatePersonUseCase updatePersonUseCase;
    private final DeletePersonUseCase deletePersonUseCase;

    @Autowired
    public PersonController(
            CreatePersonUseCase createPersonUseCase,
            GetPersonUseCase getPersonUseCase,
            UpdatePersonUseCase updatePersonUseCase,
            DeletePersonUseCase deletePersonUseCase) {
        this.createPersonUseCase = createPersonUseCase;
        this.getPersonUseCase = getPersonUseCase;
        this.updatePersonUseCase = updatePersonUseCase;
        this.deletePersonUseCase = deletePersonUseCase;
    }

    /**
     * Creates a new person.
     *
     * @param command the person creation data
     * @return ResponseEntity with the created person or error message
     */
    @PostMapping
    public ResponseEntity<?> createPerson(@Valid @RequestBody CreatePersonCommand command) {
        try {
            PersonDto createdPerson = createPersonUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while creating the person");
        }
    }

    /**
     * Retrieves a person by ID.
     *
     * @param id the person's ID
     * @return ResponseEntity with the person data or not found status
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPersonById(@PathVariable Long id) {
        try {
            PersonDto person = getPersonUseCase.getById(id);
            if (person != null) {
                return ResponseEntity.ok(person);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving the person");
        }
    }

    /**
     * Retrieves a person by document number.
     *
     * @param document the person's document number
     * @return ResponseEntity with the person data or not found status
     */
    @GetMapping("/document/{document}")
    public ResponseEntity<?> getPersonByDocument(@PathVariable String document) {
        try {
            PersonDto person = getPersonUseCase.getByDocument(document);
            if (person != null) {
                return ResponseEntity.ok(person);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving the person");
        }
    }

    /**
     * Retrieves all persons or filters by city or document type.
     *
     * @param city optional city filter
     * @param documentType optional document type filter
     * @return ResponseEntity with list of persons
     */
    @GetMapping
    public ResponseEntity<?> getAllPersons(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String documentType) {
        try {
            List<PersonDto> persons;

            if (city != null && !city.trim().isEmpty()) {
                persons = getPersonUseCase.getByCity(city);
            } else if (documentType != null && !documentType.trim().isEmpty()) {
                persons = getPersonUseCase.getByDocumentType(documentType);
            } else {
                persons = getPersonUseCase.getAll();
            }

            return ResponseEntity.ok(persons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving persons");
        }
    }

    /**
     * Updates an existing person.
     *
     * @param id the person's ID
     * @param command the person update data
     * @return ResponseEntity with the updated person or error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @Valid @RequestBody UpdatePersonCommand command) {
        try {
            command.setId(id); // Ensure the ID from path is used
            PersonDto updatedPerson = updatePersonUseCase.execute(command);
            return ResponseEntity.ok(updatedPerson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while updating the person");
        }
    }

    /**
     * Deletes a person by ID.
     *
     * @param id the person's ID
     * @return ResponseEntity with success or error status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        try {
            deletePersonUseCase.execute(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while deleting the person");
        }
    }

    /**
     * Checks if a person exists by document number.
     *
     * @param document the document number to check
     * @return ResponseEntity with existence status
     */
    @GetMapping("/exists/{document}")
    public ResponseEntity<?> checkPersonExists(@PathVariable String document) {
        try {
            boolean exists = getPersonUseCase.existsByDocument(document);
            return ResponseEntity.ok().body("{\"exists\": " + exists + "}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while checking person existence");
        }
    }

    /**
     * Gets the total count of persons in the system.
     *
     * @return ResponseEntity with the total count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getTotalPersonCount() {
        try {
            long count = getPersonUseCase.getTotalCount();
            return ResponseEntity.ok().body("{\"count\": " + count + "}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while counting persons");
        }
    }
}