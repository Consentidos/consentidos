package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import com.veterinaria.consentidos.features.documentIdentifier.domain.repository.DocumentIdentifierRepository;
import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.application.command.UpdatePersonCommand;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.exception.PersonAlreadyExistsException;
import com.veterinaria.consentidos.features.person.domain.exception.PersonNotFoundException;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdatePersonUseCase Tests")
class UpdatePersonUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DocumentIdentifierRepository documentIdentifierRepository;

    @InjectMocks
    private UpdatePersonUseCase updatePersonUseCase;

    private DocumentIdentifier diTI;
    private Person existingPerson;
    private UpdatePersonCommand updateCommand;

    @BeforeEach
    void setUp() {
        DocumentIdentifier diCC = new DocumentIdentifier("CC", "Colombia");
        diCC.setId(1L);
        diTI = new DocumentIdentifier("TI", "Colombia");
        diTI.setId(2L);

        existingPerson = new Person("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", diCC, "Medellin");
        existingPerson.setId(1L);

        updateCommand = new UpdatePersonCommand(1L, "F", "Juana", "Martinez", "Bogota", "87654321", 2L);
    }

    @Test
    @DisplayName("Should update person successfully when person exists and document is unique")
    void testExecute_PersonExistsAndDocumentUnique_ShouldUpdateSuccessfully() {
        // Given
        Person updatedPerson = new Person("F", existingPerson.getBirthDate(), "Juana", "Martinez", "87654321", diTI, "Bogota");
        updatedPerson.setId(1L);

        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));
        when(personRepository.existsByDocument("87654321")).thenReturn(false);
        when(documentIdentifierRepository.findById(2L)).thenReturn(Optional.of(diTI));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);

        // When
        PersonDto result = updatePersonUseCase.execute(updateCommand);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juana", result.getFirstName());
        assertEquals("Martinez", result.getLastName());
        assertEquals("87654321", result.getDocumentNumber());
        assertNotNull(result.getDocumentIdentifier());
        assertEquals("TI", result.getDocumentIdentifier().getDocumentType());
        assertEquals("F", result.getSex());
        assertEquals("Bogota", result.getCity());

        verify(personRepository).findById(1L);
        verify(personRepository).existsByDocument("87654321");
        verify(personRepository).save(any(Person.class));
    }

    @Test
    @DisplayName("Should update person successfully when document is not changed")
    void testExecute_DocumentNotChanged_ShouldUpdateWithoutDocumentCheck() {
        // Given
        UpdatePersonCommand sameDocumentCommand = new UpdatePersonCommand(1L, "F", "Juana", "Martinez", "Bogota", "12345678", 2L);
        Person updatedPerson = new Person("F", existingPerson.getBirthDate(), "Juana", "Martinez", "12345678", diTI, "Bogota");
        updatedPerson.setId(1L);

        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));
        when(documentIdentifierRepository.findById(2L)).thenReturn(Optional.of(diTI));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);

        // When
        PersonDto result = updatePersonUseCase.execute(sameDocumentCommand);

        // Then
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentNumber());
        assertEquals("Juana", result.getFirstName());

        verify(personRepository).findById(1L);
        verify(personRepository, never()).existsByDocument(any(String.class));
        verify(personRepository).save(any(Person.class));
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person not found")
    void testExecute_PersonNotFound_ShouldThrowException() {
        // Given
        when(personRepository.findById(999L)).thenReturn(Optional.empty());
        UpdatePersonCommand invalidCommand = new UpdatePersonCommand(999L, "F", "Test", "User", "City", "12345", 1L);

        // When & Then
        PersonNotFoundException exception = assertThrows(
            PersonNotFoundException.class,
            () -> updatePersonUseCase.execute(invalidCommand)
        );

        assertEquals("Person with ID 999 not found", exception.getMessage());
        verify(personRepository).findById(999L);
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Should throw PersonAlreadyExistsException when new document already exists")
    void testExecute_DocumentAlreadyExists_ShouldThrowException() {
        // Given
        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));
        when(personRepository.existsByDocument("87654321")).thenReturn(true);

        // When & Then
        PersonAlreadyExistsException exception = assertThrows(
            PersonAlreadyExistsException.class,
            () -> updatePersonUseCase.execute(updateCommand)
        );

        assertEquals("A person with document 87654321 already exists", exception.getMessage());
        verify(personRepository).findById(1L);
        verify(personRepository).existsByDocument("87654321");
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Should handle all field updates correctly")
    void testExecute_UpdateAllFields_ShouldUpdateAllPersonProperties() {
        // Given
        Person updatedPerson = new Person("F", existingPerson.getBirthDate(), "Juana", "Martinez", "99999999", diTI, "Cali");
        updatedPerson.setId(1L);

        UpdatePersonCommand fullUpdateCommand = new UpdatePersonCommand(1L, "F", "Juana", "Martinez", "Cali", "99999999", 2L);

        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));
        when(personRepository.existsByDocument("99999999")).thenReturn(false);
        when(documentIdentifierRepository.findById(2L)).thenReturn(Optional.of(diTI));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);

        // When
        PersonDto result = updatePersonUseCase.execute(fullUpdateCommand);

        // Then
        assertNotNull(result);
        assertEquals("F", result.getSex());
        assertEquals("Juana", result.getFirstName());
        assertEquals("Martinez", result.getLastName());
        assertEquals("Cali", result.getCity());
        assertEquals("99999999", result.getDocumentNumber());
        assertEquals("TI", result.getDocumentIdentifier().getDocumentType());

        verify(personRepository).save(argThat(person ->
            person.getSex().equals("F") &&
            person.getFirstName().equals("Juana") &&
            person.getLastName().equals("Martinez") &&
            person.getCity().equals("Cali") &&
            person.getDocumentNumber().equals("99999999") &&
            person.getDocumentType().equals("TI")
        ));
    }

    @Test
    @DisplayName("Should preserve existing birthDate when updating")
    void testExecute_UpdatePerson_ShouldPreserveBirthDate() {
        // Given
        LocalDateTime originalBirthDate = LocalDateTime.of(1990, 1, 15, 0, 0);
        Person updatedPerson = new Person("F", originalBirthDate, "Juana", "Martinez", "87654321", diTI, "Bogota");
        updatedPerson.setId(1L);

        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));
        when(personRepository.existsByDocument("87654321")).thenReturn(false);
        when(documentIdentifierRepository.findById(2L)).thenReturn(Optional.of(diTI));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);

        // When
        PersonDto result = updatePersonUseCase.execute(updateCommand);

        // Then
        assertNotNull(result);
        assertEquals(originalBirthDate, result.getBirthDate());

        verify(personRepository).save(argThat(person -> person.getBirthDate().equals(originalBirthDate)));
    }
}
