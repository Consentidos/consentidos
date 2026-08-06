package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import com.veterinaria.consentidos.features.person.application.dto.PersonDto;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.entity.PersonDocument;
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
        DocumentType dt1 = new DocumentType("CC", "Colombia");
        dt1.setId(1L);
        DocumentType dt2 = new DocumentType("TI", "Colombia");
        dt2.setId(2L);

        testPerson1 = new Person();
        testPerson1.setSex("M"); testPerson1.setBirthDate(LocalDateTime.of(1990, 1, 15, 0, 0));
        testPerson1.setFirstName("Juan"); testPerson1.setLastName("Perez"); testPerson1.setCity("Medellin");
        testPerson1.setId(1L);
        testPerson1.addDocument(new PersonDocument(testPerson1, "12345678", dt1));

        testPerson2 = new Person();
        testPerson2.setSex("F"); testPerson2.setBirthDate(LocalDateTime.of(1985, 6, 20, 0, 0));
        testPerson2.setFirstName("Maria"); testPerson2.setLastName("Gonzalez"); testPerson2.setCity("Bogota");
        testPerson2.setId(2L);
        testPerson2.addDocument(new PersonDocument(testPerson2, "87654321", dt2));
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
        assertEquals("12345678", result.getDocumentNumber());
        
        verify(personRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person does not exist by ID")
    void testGetByIdPersonNotExistsShouldThrowException() {
        // Given
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PersonNotFoundException.class, () -> getPersonUseCase.getById(999L));
        verify(personRepository).findById(999L);
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
        assertEquals("CC", result.get(0).getDocumentType().getCode());
        
        verify(personRepository).findByDocumentType("CC");
    }
}