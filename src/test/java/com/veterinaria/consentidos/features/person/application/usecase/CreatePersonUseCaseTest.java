package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.person.application.command.CreatePersonCommand;
import com.veterinaria.consentidos.features.person.application.command.PersonDto;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for CreatePersonUseCase.
 * Tests the business logic for creating new persons.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePersonUseCase Tests")
class CreatePersonUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private CreatePersonUseCase createPersonUseCase;

    private CreatePersonCommand validCommand;
    private Person savedPerson;

    @BeforeEach
    void setUp() {
        validCommand = new CreatePersonCommand(
            "M", 
            LocalDateTime.of(1990, 1, 15, 0, 0), 
            "Juan", 
            "Perez", 
            "Medellin", 
            "12345678", 
            "CC"
        );

        savedPerson = new Person("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
        savedPerson.setId(1L);
    }

    @Test
    @DisplayName("Should successfully create person when command is valid")
    void testExecute_ValidCommand_ShouldCreatePerson() {
        // Given
        when(personRepository.existsByDocument("12345678")).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        // When
        PersonDto result = createPersonUseCase.execute(validCommand);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("M", result.getSex());
        assertEquals("Juan", result.getFirstName());
        assertEquals("Perez", result.getLastName());
        assertEquals("Medellin", result.getCity());
        assertEquals("12345678", result.getDocument());
        assertEquals("CC", result.getDocumentType());
        assertEquals(LocalDateTime.of(1990, 1, 15, 0, 0), result.getBirthDate());

        verify(personRepository).existsByDocument("12345678");
        verify(personRepository).save(any(Person.class));
    }

    @Test
    @DisplayName("Should throw exception when person with same document already exists")
    void testExecute_DuplicateDocument_ShouldThrowException() {
        // Given
        when(personRepository.existsByDocument("12345678")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createPersonUseCase.execute(validCommand)
        );

        assertEquals("A person with document 12345678 already exists", exception.getMessage());
        verify(personRepository).existsByDocument("12345678");
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Should call repository methods with correct parameters")
    void testExecute_ShouldCallRepositoryWithCorrectParameters() {
        // Given
        when(personRepository.existsByDocument("12345678")).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        // When
        createPersonUseCase.execute(validCommand);

        // Then
        verify(personRepository).save(argThat(person -> 
            person.getSex().equals("M") &&
            person.getFirstName().equals("Juan") &&
            person.getLastName().equals("Perez") &&
            person.getCity().equals("Medellin") &&
            person.getDocument().equals("12345678") &&
            person.getDocumentType().equals("CC") &&
            person.getBirthDate().equals(LocalDateTime.of(1990, 1, 15, 0, 0))
        ));
    }

    @Test
    @DisplayName("Should handle null command gracefully")
    void testExecute_NullCommand_ShouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> createPersonUseCase.execute(null));
    }
}