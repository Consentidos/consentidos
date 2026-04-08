package com.veterinaria.consentidos.features.person.application.usecase;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for GetPersonUseCase.
 * Tests the business logic for retrieving person information.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetPersonUseCase Tests")
class GetPersonUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private GetPersonUseCase getPersonUseCase;

    private Person testPerson1;
    private Person testPerson2;

    @BeforeEach
    void setUp() {
        testPerson1 = new Person("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
        testPerson1.setId(1L);

        testPerson2 = new Person("F", LocalDateTime.of(1985, 6, 20, 0, 0), "Maria", "Gonzalez", "87654321", "TI", "Bogota");
        testPerson2.setId(2L);
    }

    @Test
    @DisplayName("Should return PersonDto when person exists by ID")
    void testGetById_PersonExists_ShouldReturnPersonDto() {
        // Given
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson1));

        // When
        PersonDto result = getPersonUseCase.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getFirstName());
        assertEquals("Perez", result.getLastName());
        assertEquals("12345678", result.getDocument());
        
        verify(personRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return null when person does not exist by ID")
    void testGetById_PersonNotExists_ShouldReturnNull() {
        // Given
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        PersonDto result = getPersonUseCase.getById(999L);

        // Then
        assertNull(result);
        verify(personRepository).findById(999L);
    }

    @Test
    @DisplayName("Should return PersonDto when person exists by document")
    void testGetByDocument_PersonExists_ShouldReturnPersonDto() {
        // Given
        when(personRepository.findByDocument("12345678")).thenReturn(Optional.of(testPerson1));

        // When
        PersonDto result = getPersonUseCase.getByDocument("12345678");

        // Then
        assertNotNull(result);
        assertEquals("12345678", result.getDocument());
        assertEquals("Juan", result.getFirstName());
        
        verify(personRepository).findByDocument("12345678");
    }

    @Test
    @DisplayName("Should return null when person does not exist by document")
    void testGetByDocument_PersonNotExists_ShouldReturnNull() {
        // Given
        when(personRepository.findByDocument("999999")).thenReturn(Optional.empty());

        // When
        PersonDto result = getPersonUseCase.getByDocument("999999");

        // Then
        assertNull(result);
        verify(personRepository).findByDocument("999999");
    }

    @Test
    @DisplayName("Should return all persons when persons exist")
    void testGetAll_PersonsExist_ShouldReturnAllPersons() {
        // Given
        List<Person> persons = Arrays.asList(testPerson1, testPerson2);
        when(personRepository.findAll()).thenReturn(persons);

        // When
        List<PersonDto> result = getPersonUseCase.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
        assertEquals("Maria", result.get(1).getFirstName());
        
        verify(personRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no persons exist")
    void testGetAll_NoPersons_ShouldReturnEmptyList() {
        // Given
        when(personRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PersonDto> result = getPersonUseCase.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(personRepository).findAll();
    }

    @Test
    @DisplayName("Should return persons by city")
    void testGetByCity_PersonsExistInCity_ShouldReturnPersons() {
        // Given
        List<Person> persons = Arrays.asList(testPerson1);
        when(personRepository.findByCity("Medellin")).thenReturn(persons);

        // When
        List<PersonDto> result = getPersonUseCase.getByCity("Medellin");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Medellin", result.get(0).getCity());
        assertEquals("Juan", result.get(0).getFirstName());
        
        verify(personRepository).findByCity("Medellin");
    }

    @Test
    @DisplayName("Should return empty list when no persons in city")
    void testGetByCity_NoPersonsInCity_ShouldReturnEmptyList() {
        // Given
        when(personRepository.findByCity("Cali")).thenReturn(Collections.emptyList());

        // When
        List<PersonDto> result = getPersonUseCase.getByCity("Cali");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(personRepository).findByCity("Cali");
    }

    @Test
    @DisplayName("Should return persons by document type")
    void testGetByDocumentType_PersonsExistWithDocType_ShouldReturnPersons() {
        // Given
        List<Person> persons = Arrays.asList(testPerson1);
        when(personRepository.findByDocumentType("CC")).thenReturn(persons);

        // When
        List<PersonDto> result = getPersonUseCase.getByDocumentType("CC");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CC", result.get(0).getDocumentType());
        
        verify(personRepository).findByDocumentType("CC");
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void testGetByDocument_NullDocument_ShouldCallRepository() {
        // Given
        when(personRepository.findByDocument(null)).thenReturn(Optional.empty());

        // When
        PersonDto result = getPersonUseCase.getByDocument(null);

        // Then
        assertNull(result);
        verify(personRepository).findByDocument(null);
    }
}