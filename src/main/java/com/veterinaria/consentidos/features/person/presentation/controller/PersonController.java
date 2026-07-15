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

/**
 * REST controller for Person management.
 * Exceptions bubble up to the global ErrorHandler; no try/catch needed here.
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

    @PostMapping
    public ResponseEntity<PersonDto> createPerson(@Valid @RequestBody CreatePersonCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createPersonUseCase.execute(command));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(getPersonUseCase.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PersonDto>> getAllPersons(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String documentType) {
        if (city != null && !city.isBlank()) {
            return ResponseEntity.ok(getPersonUseCase.getByCity(city));
        }
        if (documentType != null && !documentType.isBlank()) {
            return ResponseEntity.ok(getPersonUseCase.getByDocumentType(documentType));
        }
        return ResponseEntity.ok(getPersonUseCase.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDto> updatePerson(
            @PathVariable Long id, @Valid @RequestBody UpdatePersonCommand command) {
        command.setId(id);
        return ResponseEntity.ok(updatePersonUseCase.execute(command));
    }

    @PostMapping("/search")
    public ResponseEntity<PagedResult<PersonDto>> searchPersons(@RequestBody PersonSearchCriteria criteria) {
        return ResponseEntity.ok(searchPersonUseCase.execute(criteria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        deletePersonUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
