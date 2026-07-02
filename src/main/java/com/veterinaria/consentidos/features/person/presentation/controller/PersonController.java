package com.veterinaria.consentidos.features.person.presentation.controller;

import com.veterinaria.consentidos.core.PagedResult;
import com.veterinaria.consentidos.features.person.application.command.CreatePersonCommand;
import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.application.command.UpdatePersonCommand;
import com.veterinaria.consentidos.features.person.application.usecase.CreatePersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.DeletePersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.GetPersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.SearchPersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.UpdatePersonUseCase;
import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
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
import java.util.Map;

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
    private final SearchPersonUseCase searchPersonUseCase;

    @Autowired
    public PersonController(
            CreatePersonUseCase createPersonUseCase,
            GetPersonUseCase getPersonUseCase,
            UpdatePersonUseCase updatePersonUseCase,
            DeletePersonUseCase deletePersonUseCase,
            SearchPersonUseCase searchPersonUseCase) {
        this.createPersonUseCase = createPersonUseCase;
        this.getPersonUseCase = getPersonUseCase;
        this.updatePersonUseCase = updatePersonUseCase;
        this.deletePersonUseCase = deletePersonUseCase;
        this.searchPersonUseCase = searchPersonUseCase;
    }

    /**
     * Creates a new person.
     *
     * @param command the person creation data
     * @return ResponseEntity with the created person or error message
     */
    @PostMapping
    public ResponseEntity<?> createPerson(@Valid @RequestBody CreatePersonCommand command) {
        PersonDto createdPerson = createPersonUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);
    }

    /**
     * Retrieves a person by ID.
     *
     * @param id the person's ID
     * @return ResponseEntity with the person data or not found status
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPersonById(@PathVariable Long id) {
        PersonDto person = getPersonUseCase.getById(id);
        if (person != null) {
            return ResponseEntity.ok(person);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves a person by document number.
     *
     * @param document the person's document number
     * @return ResponseEntity with the person data or not found status
     */
    @GetMapping("/document/{document}")
    public ResponseEntity<?> getPersonByDocument(@PathVariable String document) {
        PersonDto person = getPersonUseCase.getByDocument(document);
        if (person != null) {
            return ResponseEntity.ok(person);
        }
        return ResponseEntity.notFound().build();
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
        List<PersonDto> persons;
        if (city != null && !city.trim().isEmpty()) {
            persons = getPersonUseCase.getByCity(city);
        } else if (documentType != null && !documentType.trim().isEmpty()) {
            persons = getPersonUseCase.getByDocumentType(documentType);
        } else {
            persons = getPersonUseCase.getAll();
        }
        return ResponseEntity.ok(persons);
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
        command.setId(id);
        PersonDto updatedPerson = updatePersonUseCase.execute(command);
        return ResponseEntity.ok(updatedPerson);
    }

    /**
     * Searches persons using dynamic criteria with AND composition.
     * All fields are optional; only provided fields are applied as filters.
     * Text fields use case-insensitive LIKE matching; date fields define a range.
     *
     * @param criteria the search criteria
     * @return ResponseEntity with the list of matching persons
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchPersons(@RequestBody PersonSearchCriteria criteria) {
        PagedResult<PersonDto> result = searchPersonUseCase.execute(criteria);
        return ResponseEntity.ok(result);
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
        boolean exists = getPersonUseCase.existsByDocument(document);
        return ResponseEntity.ok().body(Map.of("exists", exists));
    }

    /**
     * Gets the total count of persons in the system.
     *
     * @return ResponseEntity with the total count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getTotalPersonCount() {
        long count = getPersonUseCase.getTotalCount();
        return ResponseEntity.ok().body(Map.of("count", count));
    }
}