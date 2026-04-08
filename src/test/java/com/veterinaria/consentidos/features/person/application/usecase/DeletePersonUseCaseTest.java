package com.veterinaria.consentidos.features.person.application.usecase;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

/**
 * Test class for DeletePersonUseCase.
 * Tests the business logic for deleting person records.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeletePersonUseCase Tests")
class DeletePersonUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private DeletePersonUseCase deletePersonUseCase;

    private Person existingPerson;

    @BeforeEach
    void setUp() {
        existingPerson = new Person("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
        existingPerson.setId(1L);
    }

    @Test
    @DisplayName("Should delete person successfully when person exists")
    void testExecute_PersonExists_ShouldDeleteSuccessfully() {
        // Given
        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));

        // When
        assertDoesNotThrow(() -> deletePersonUseCase.execute(1L));

        // Then
        verify(personRepository).findById(1L);
        verify(personRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when person does not exist")
    void testExecute_PersonNotFound_ShouldThrowException() {
        // Given
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deletePersonUseCase.execute(999L)
        );

        assertEquals("Person with ID 999 not found", exception.getMessage());
        verify(personRepository).findById(999L);
        verify(personRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("Should return true when person can be deleted (exists)")
    void testCanDelete_PersonExists_ShouldReturnTrue() {
        // Given
        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));

        // When
        boolean result = deletePersonUseCase.canDelete(1L);

        // Then
        assertTrue(result);
        verify(personRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return false when person cannot be deleted (does not exist)")
    void testCanDelete_PersonNotExists_ShouldReturnFalse() {
        // Given
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = deletePersonUseCase.canDelete(999L);

        // Then
        assertFalse(result);
        verify(personRepository).findById(999L);
    }

    @Test
    @DisplayName("Should handle null ID gracefully in canDelete")
    void testCanDelete_NullId_ShouldReturnFalse() {
        // Given
        when(personRepository.findById(null)).thenReturn(Optional.empty());

        // When
        boolean result = deletePersonUseCase.canDelete(null);

        // Then
        assertFalse(result);
        verify(personRepository).findById(null);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when trying to delete with null ID")
    void testExecute_NullId_ShouldThrowException() {
        // Given
        when(personRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deletePersonUseCase.execute(null)
        );

        assertEquals("Person with ID null not found", exception.getMessage());
        verify(personRepository).findById(null);
        verify(personRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should call repository methods in correct order during execution")
    void testExecute_CallOrder_ShouldCallFindBeforeDelete() {
        // Given
        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));

        // When
        deletePersonUseCase.execute(1L);

        // Then
        InOrder inOrder = inOrder(personRepository);
        inOrder.verify(personRepository).findById(1L);
        inOrder.verify(personRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle negative IDs appropriately")
    void testExecute_NegativeId_ShouldThrowException() {
        // Given
        when(personRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deletePersonUseCase.execute(-1L)
        );

        assertEquals("Person with ID -1 not found", exception.getMessage());
        verify(personRepository).findById(-1L);
    }
}