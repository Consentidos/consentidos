package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.entity.PersonDocument;

/**
 * Test class for PersonRepositoryImpl.
 * Tests the repository implementation methods using mocked JPA repository.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PersonRepositoryImpl Tests")
class PersonRepositoryImplTest {

    @Mock
    private PersonJpaRepository personJpaRepository;

    @Mock
    private PersonQueryBuilder queryBuilder;

    @InjectMocks
    private PersonRepositoryImpl personRepository;

    private Person testPerson;
    private Person anotherPerson;

    @BeforeEach
    void setUp() {
        DocumentType dtCC = new DocumentType("CC", "Colombia");
        dtCC.setId(1L);
        DocumentType dtTI = new DocumentType("TI", "Colombia");
        dtTI.setId(2L);

        testPerson = new Person();
        testPerson.setSex("M"); testPerson.setBirthDate(LocalDateTime.of(1990, 1, 15, 0, 0));
        testPerson.setFirstName("Juan"); testPerson.setLastName("Perez"); testPerson.setCity("Medellin");
        testPerson.setId(1L);
        testPerson.addDocument(new PersonDocument(testPerson, "12345678", dtCC));

        anotherPerson = new Person();
        anotherPerson.setSex("F"); anotherPerson.setBirthDate(LocalDateTime.of(1985, 6, 20, 0, 0));
        anotherPerson.setFirstName("Maria"); anotherPerson.setLastName("Gonzalez"); anotherPerson.setCity("Bogota");
        anotherPerson.setId(2L);
        anotherPerson.addDocument(new PersonDocument(anotherPerson, "87654321", dtTI));
    }

    @Test
    @DisplayName("Should save person successfully")
    void testSave() {
        // Given
        when(personJpaRepository.save(any(Person.class))).thenReturn(testPerson);

        // When
        Person savedPerson = personRepository.save(testPerson);

        // Then
        assertNotNull(savedPerson);
        assertEquals(testPerson.getId(), savedPerson.getId());
        assertEquals(testPerson.getDocumentNumber(), savedPerson.getDocumentNumber());
        verify(personJpaRepository, times(1)).save(testPerson);
    }

    @Test
    @DisplayName("Should find person by ID when exists")
    void testFindByIdWhenExists() {
        // Given
        Long personId = 1L;
        when(personJpaRepository.findById(personId)).thenReturn(Optional.of(testPerson));

        // When
        Optional<Person> foundPerson = personRepository.findById(personId);

        // Then
        assertTrue(foundPerson.isPresent());
        assertEquals(testPerson.getId(), foundPerson.get().getId());
        assertEquals(testPerson.getDocumentNumber(), foundPerson.get().getDocumentNumber());
        verify(personJpaRepository, times(1)).findById(personId);
    }

    @Test
    @DisplayName("Should return empty when person not found by ID")
    void testFindByIdWhenNotExists() {
        // Given
        Long personId = 999L;
        when(personJpaRepository.findById(personId)).thenReturn(Optional.empty());

        // When
        Optional<Person> foundPerson = personRepository.findById(personId);

        // Then
        assertFalse(foundPerson.isPresent());
        verify(personJpaRepository, times(1)).findById(personId);
    }

    @Test
    @DisplayName("Should find person by document when exists")
    void testFindByDocumentWhenExists() {
        // Given
        String document = "12345678";
        when(personJpaRepository.findByDocuments_DocumentNumber(document)).thenReturn(Optional.of(testPerson));

        // When
        Optional<Person> foundPerson = personRepository.findByDocument(document);

        // Then
        assertTrue(foundPerson.isPresent());
        assertEquals(testPerson.getDocumentNumber(), foundPerson.get().getDocumentNumber());
        verify(personJpaRepository, times(1)).findByDocuments_DocumentNumber(document);
    }

    @Test
    @DisplayName("Should return empty when person not found by document")
    void testFindByDocumentWhenNotExists() {
        // Given
        String document = "99999999";
        when(personJpaRepository.findByDocuments_DocumentNumber(document)).thenReturn(Optional.empty());

        // When
        Optional<Person> foundPerson = personRepository.findByDocument(document);

        // Then
        assertFalse(foundPerson.isPresent());
        verify(personJpaRepository, times(1)).findByDocuments_DocumentNumber(document);
    }

    @Test
    @DisplayName("Should find all persons")
    void testFindAll() {
        // Given
        List<Person> persons = Arrays.asList(testPerson, anotherPerson);
        when(personJpaRepository.findAll()).thenReturn(persons);

        // When
        List<Person> foundPersons = personRepository.findAll();

        // Then
        assertNotNull(foundPersons);
        assertEquals(2, foundPersons.size());
        assertTrue(foundPersons.contains(testPerson));
        assertTrue(foundPersons.contains(anotherPerson));
        verify(personJpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no persons exist")
    void testFindAllWhenEmpty() {
        // Given
        when(personJpaRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Person> foundPersons = personRepository.findAll();

        // Then
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty());
        verify(personJpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find persons by city")
    void testFindByCity() {
        // Given
        String city = "Medellin";
        List<Person> persons = Arrays.asList(testPerson);
        when(personJpaRepository.findByCityIgnoreCase(city)).thenReturn(persons);

        // When
        List<Person> foundPersons = personRepository.findByCity(city);

        // Then
        assertNotNull(foundPersons);
        assertEquals(1, foundPersons.size());
        assertEquals(testPerson.getCity(), foundPersons.get(0).getCity());
        verify(personJpaRepository, times(1)).findByCityIgnoreCase(city);
    }

    @Test
    @DisplayName("Should find persons by document type")
    void testFindByDocumentType() {
        // Given
        String documentType = "CC";
        List<Person> persons = Arrays.asList(testPerson);
        when(personJpaRepository.findByDocuments_DocumentType_CodeIgnoreCaseAndDocuments_IsActiveTrue(documentType)).thenReturn(persons);

        // When
        List<Person> foundPersons = personRepository.findByDocumentType(documentType);

        // Then
        assertNotNull(foundPersons);
        assertEquals(1, foundPersons.size());
        assertEquals(testPerson.getDocumentType().getCode(), foundPersons.get(0).getDocumentType().getCode());
        verify(personJpaRepository, times(1)).findByDocuments_DocumentType_CodeIgnoreCaseAndDocuments_IsActiveTrue(documentType);
    }

    @Test
    @DisplayName("Should return true when person exists by document")
    void testExistsByDocumentWhenExists() {
        // Given
        String document = "12345678";
        when(personJpaRepository.existsByDocuments_DocumentNumber(document)).thenReturn(true);

        // When
        boolean exists = personRepository.existsByDocument(document);

        // Then
        assertTrue(exists);
        verify(personJpaRepository, times(1)).existsByDocuments_DocumentNumber(document);
    }

    @Test
    @DisplayName("Should return false when person does not exist by document")
    void testExistsByDocumentWhenNotExists() {
        // Given
        String document = "99999999";
        when(personJpaRepository.existsByDocuments_DocumentNumber(document)).thenReturn(false);

        // When
        boolean exists = personRepository.existsByDocument(document);

        // Then
        assertFalse(exists);
        verify(personJpaRepository, times(1)).existsByDocuments_DocumentNumber(document);
    }

    @Test
    @DisplayName("Should delete person by ID")
    void testDeleteById() {
        // Given
        Long personId = 1L;
        doNothing().when(personJpaRepository).deleteById(personId);

        // When
        personRepository.deleteById(personId);

        // Then
        verify(personJpaRepository, times(1)).deleteById(personId);
    }

    @Test
    @DisplayName("Should return correct count of persons")
    void testCount() {
        // Given
        long expectedCount = 5L;
        when(personJpaRepository.count()).thenReturn(expectedCount);

        // When
        long actualCount = personRepository.count();

        // Then
        assertEquals(expectedCount, actualCount);
        verify(personJpaRepository, times(1)).count();
    }

    @Test
    @DisplayName("Should handle null person in save")
    void testSaveWithNullPerson() {
        // Given
        when(personJpaRepository.save(null)).thenThrow(new IllegalArgumentException("Person cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> personRepository.save(null));
        verify(personJpaRepository, times(1)).save(null);
    }

    @Test
    @DisplayName("Should handle null document in findByDocument")
    void testFindByDocumentWithNull() {
        // Given
        when(personJpaRepository.findByDocuments_DocumentNumber(null)).thenReturn(Optional.empty());

        // When
        Optional<Person> foundPerson = personRepository.findByDocument(null);

        // Then
        assertFalse(foundPerson.isPresent());
        verify(personJpaRepository, times(1)).findByDocuments_DocumentNumber(null);
    }

    @Test
    @DisplayName("Should handle empty string in findByCity")
    void testFindByCityWithEmptyString() {
        // Given
        String emptyCity = "";
        when(personJpaRepository.findByCityIgnoreCase(emptyCity)).thenReturn(Arrays.asList());

        // When
        List<Person> foundPersons = personRepository.findByCity(emptyCity);

        // Then
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty());
        verify(personJpaRepository, times(1)).findByCityIgnoreCase(emptyCity);
    }

    @Test
    @DisplayName("Should verify repository uses case insensitive search for city")
    void testFindByCityIgnoreCase() {
        // Given
        String cityUppercase = "MEDELLIN";
        List<Person> persons = Arrays.asList(testPerson);
        when(personJpaRepository.findByCityIgnoreCase(cityUppercase)).thenReturn(persons);

        // When
        List<Person> foundPersons = personRepository.findByCity(cityUppercase);

        // Then
        assertNotNull(foundPersons);
        assertEquals(1, foundPersons.size());
        verify(personJpaRepository, times(1)).findByCityIgnoreCase(cityUppercase);
    }
}