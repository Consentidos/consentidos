package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.core.PagedResult;
import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import com.veterinaria.consentidos.features.documentIdentifier.infrastructure.persistence.DocumentIdentifierJpaRepository;
import com.veterinaria.consentidos.features.documentIdentifier.infrastructure.persistence.DocumentIdentifierRepositoryImpl;
import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({PersonRepositoryImpl.class, PersonQueryBuilder.class, DocumentIdentifierRepositoryImpl.class})
@DisplayName("PersonRepositoryImpl Search Integration Tests")
class PersonRepositoryImplSearchTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PersonRepositoryImpl personRepository;

    private static final LocalDateTime DATE_1990 = LocalDateTime.of(1990, 1, 15, 0, 0);
    private static final LocalDateTime DATE_1985 = LocalDateTime.of(1985, 6, 20, 0, 0);
    private static final LocalDateTime DATE_1995 = LocalDateTime.of(1995, 3, 10, 0, 0);

    private DocumentIdentifier diCC;
    private DocumentIdentifier diTI;

    @BeforeEach
    void setUp() {
        diCC = testEntityManager.persistAndFlush(new DocumentIdentifier("CC", "Colombia"));
        diTI = testEntityManager.persistAndFlush(new DocumentIdentifier("TI", "Colombia"));

        testEntityManager.persistAndFlush(
                new Person("M", DATE_1990, "Juan", "Perez", "12345678", diCC, "Medellin"));
        testEntityManager.persistAndFlush(
                new Person("F", DATE_1985, "Maria", "Gonzalez", "87654321", diTI, "Bogota"));
        testEntityManager.persistAndFlush(
                new Person("M", DATE_1995, "Carlos", "Perez", "11223344", diCC, "Medellin"));
    }

    @Test
    @DisplayName("Should return all persons when all criteria fields are null")
    void testSearch_NullCriteria_ShouldReturnAllPersons() {
        PersonSearchCriteria criteria = new PersonSearchCriteria();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(3L, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
    }

    @Test
    @DisplayName("Should return all persons when all text criteria fields are blank")
    void testSearch_BlankCriteria_ShouldReturnAllPersons() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .firstName("  ")
                .lastName("  ")
                .documentNumber("  ")
                .sex("  ")
                .city("  ")
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(3L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should filter by firstName using case-insensitive LIKE")
    void testSearch_WithFirstName_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().firstName("JUAN").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should filter by lastName using case-insensitive LIKE and match multiple persons")
    void testSearch_WithLastName_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().lastName("perez").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(2L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should filter by partial document using LIKE")
    void testSearch_WithDocument_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().documentNumber("8765").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("87654321", result.getContent().get(0).getDocumentNumber());
    }

    @Test
    @DisplayName("Should filter by documentIdentifierId using exact match")
    void testSearch_WithDocumentType_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().documentIdentifierId(diCC.getId()).build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(2L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should filter by sex using exact match")
    void testSearch_WithSex_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().sex("F").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("F", result.getContent().get(0).getSex());
    }

    @Test
    @DisplayName("Should filter by city using case-insensitive LIKE")
    void testSearch_WithCity_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().city("BOGOTA").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Bogota", result.getContent().get(0).getCity());
    }

    @Test
    @DisplayName("Should filter by birthDateFrom (greaterThanOrEqualTo)")
    void testSearch_WithBirthDateFrom_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .birthDateFrom(LocalDateTime.of(1990, 1, 1, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(2L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should filter by birthDateTo (lessThanOrEqualTo)")
    void testSearch_WithBirthDateTo_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .birthDateTo(LocalDateTime.of(1987, 12, 31, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Maria", result.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should filter by birthDate range combining from and to")
    void testSearch_WithBirthDateRange_ShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .birthDateFrom(LocalDateTime.of(1989, 1, 1, 0, 0))
                .birthDateTo(LocalDateTime.of(1993, 12, 31, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should return correct page and calculate totalPages for paginated results")
    void testSearch_WithPagination_ShouldReturnCorrectPage() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .page(1)
                .size(2)
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(3L, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getPage());
        assertEquals(2, result.getSize());
    }

    @Test
    @DisplayName("Should return empty content when page is beyond available results")
    void testSearch_WithPageBeyondResults_ShouldReturnEmptyContent() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .page(10)
                .size(20)
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(3L, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should apply all criteria simultaneously")
    void testSearch_WithAllCriteria_ShouldApplyAllFilters() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .firstName("Juan")
                .lastName("Perez")
                .documentNumber("12345678")
                .documentIdentifierId(diCC.getId())
                .sex("M")
                .city("Medellin")
                .birthDateFrom(LocalDateTime.of(1989, 1, 1, 0, 0))
                .birthDateTo(LocalDateTime.of(1991, 12, 31, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getFirstName());
    }
}
