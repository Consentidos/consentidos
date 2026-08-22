package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.core.PagedResult;
import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.entity.PersonDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({PersonRepositoryImpl.class, PersonQueryBuilder.class})
@DisplayName("PersonRepositoryImpl Search Integration Tests")
class PersonRepositoryImplSearchTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PersonRepositoryImpl personRepository;

    private static final LocalDateTime DATE_1990 = LocalDateTime.of(1990, Month.JANUARY, 15, 0, 0);
    private static final LocalDateTime DATE_1985 = LocalDateTime.of(1985, Month.JUNE, 20, 0, 0);
    private static final LocalDateTime DATE_1995 = LocalDateTime.of(1995, Month.MARCH, 10, 0, 0);
    private static final String CITY_MEDELLIN = "Medellin";
    private static final String CITY_BOGOTA = "Bogota";
    private static final String LAST_PEREZ = "Perez";

    private DocumentType dtCC;

    private Person persistPerson(String sex, LocalDateTime birthDate, String firstName,
            String lastName, String docNumber, DocumentType docType, String city) {
        Person person = new Person();
        person.setSex(sex); person.setBirthDate(birthDate);
        person.setFirstName(firstName); person.setLastName(lastName); person.setCity(city);
        PersonDocument doc = new PersonDocument(person, docNumber, docType);
        person.addDocument(doc);
        return testEntityManager.persistAndFlush(person);
    }

    @BeforeEach
    void setUp() {
        dtCC = testEntityManager.persistAndFlush(new DocumentType("CC", "Colombia"));
        DocumentType dtTI = testEntityManager.persistAndFlush(new DocumentType("TI", "Colombia"));

        persistPerson("M", DATE_1990, "Juan", LAST_PEREZ, "12345678", dtCC, CITY_MEDELLIN);
        persistPerson("F", DATE_1985, "Maria", "Gonzalez", "87654321", dtTI, CITY_BOGOTA);
        persistPerson("M", DATE_1995, "Carlos", LAST_PEREZ, "11223344", dtCC, CITY_MEDELLIN);
    }

    @Test
    @DisplayName("Should return all persons when all criteria fields are null")
    void testSearchNullCriteriaShouldReturnAllPersons() {
        PersonSearchCriteria criteria = new PersonSearchCriteria();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(3L, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
    }

    @Test
    @DisplayName("Should return all persons when all text criteria fields are blank")
    void testSearchBlankCriteriaShouldReturnAllPersons() {
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
    void testSearchWithFirstNameShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().firstName("JUAN").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should filter by lastName using case-insensitive LIKE and match multiple persons")
    void testSearchWithLastNameShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().lastName("perez").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(2L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should filter by partial document using LIKE")
    void testSearchWithDocumentShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().documentNumber("8765").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("87654321", result.getContent().get(0).getDocumentNumber());
    }

    @Test
    @DisplayName("Should filter by documentTypeId using exact match")
    void testSearchWithDocumentTypeShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().documentTypeId(dtCC.getId()).build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(2L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should filter by sex using exact match")
    void testSearchWithSexShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().sex("F").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("F", result.getContent().get(0).getSex());
    }

    @Test
    @DisplayName("Should filter by city using case-insensitive LIKE")
    void testSearchWithCityShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().city("BOGOTA").build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals(CITY_BOGOTA, result.getContent().get(0).getCity());
    }

    @Test
    @DisplayName("Should filter by birthDateFrom (greaterThanOrEqualTo)")
    void testSearchWithBirthDateFromShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .birthDateFrom(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(2L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should filter by birthDateTo (lessThanOrEqualTo)")
    void testSearchWithBirthDateToShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .birthDateTo(LocalDateTime.of(1987, Month.DECEMBER, 31, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Maria", result.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should filter by birthDate range combining from and to")
    void testSearchWithBirthDateRangeShouldFilterResults() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .birthDateFrom(LocalDateTime.of(1989, Month.JANUARY, 1, 0, 0))
                .birthDateTo(LocalDateTime.of(1993, Month.DECEMBER, 31, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should return correct page and calculate totalPages for paginated results")
    void testSearchWithPaginationShouldReturnCorrectPage() {
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
    void testSearchWithPageBeyondResultsShouldReturnEmptyContent() {
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
    void testSearchWithAllCriteriaShouldApplyAllFilters() {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder()
                .firstName("Juan")
                .lastName(LAST_PEREZ)
                .documentNumber("12345678")
                .documentTypeId(dtCC.getId())
                .sex("M")
                .city(CITY_MEDELLIN)
                .birthDateFrom(LocalDateTime.of(1989, Month.JANUARY, 1, 0, 0))
                .birthDateTo(LocalDateTime.of(1991, Month.DECEMBER, 31, 0, 0))
                .build();

        PagedResult<Person> result = personRepository.search(criteria);

        assertEquals(1L, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getFirstName());
    }
}
