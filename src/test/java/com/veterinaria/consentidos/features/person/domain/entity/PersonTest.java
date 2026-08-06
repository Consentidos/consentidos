package com.veterinaria.consentidos.features.person.domain.entity;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Test class for Person entity.
 * Tests validation, constructors, equals/hashCode, and getters/setters.
 */
@DisplayName("Person Entity Tests")
class PersonTest {

    private static final DocumentType DT_CC = new DocumentType("CC", "Colombia");
    private static final DocumentType DT_TI = new DocumentType("TI", "Colombia");
    private static final String DOC_JUAN = "12345678";
    private static final String DOC_MARIA = "87654321";
    private static final String CITY_MEDELLIN = "Medellin";
    private static final String CITY_BOGOTA = "Bogota";
    private static final String FIRST_JUAN = "Juan";
    private static final String FIRST_MARIA = "Maria";
    private static final String LAST_PEREZ = "Perez";
    private static final String LAST_GONZALEZ = "Gonzalez";
    private static final LocalDateTime DATE_1990 = LocalDateTime.of(1990, Month.JANUARY, 15, 0, 0);
    private static final LocalDateTime DATE_1985 = LocalDateTime.of(1985, Month.JUNE, 20, 0, 0);

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Person createPerson(String sex, LocalDateTime birthDate, String firstName,
            String lastName, String docNumber, DocumentType docType, String city) {
        Person person = new Person();
        person.setSex(sex);
        person.setBirthDate(birthDate);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setCity(city);
        PersonDocument doc = new PersonDocument(person, docNumber, docType);
        person.addDocument(doc);
        return person;
    }

    @Test
    @DisplayName("Should create person with default constructor")
    void testDefaultConstructor() {
        Person person = new Person();
        assertNull(person.getId());
        assertNull(person.getSex());
        assertNull(person.getFirstName());
        assertNull(person.getLastName());
        assertNull(person.getBirthDate());
        assertNull(person.getCity());
        assertNull(person.getActiveDocument());
    }

    @Test
    @DisplayName("Should create person with helper and verify fields")
    void testParameterizedConstructor() {
        Person person = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);

        assertNull(person.getId());
        assertEquals("M", person.getSex());
        assertEquals(FIRST_JUAN, person.getFirstName());
        assertEquals(LAST_PEREZ, person.getLastName());
        assertEquals(CITY_MEDELLIN, person.getCity());
        assertEquals(DOC_JUAN, person.getDocumentNumber());
        assertEquals("CC", person.getDocumentType().getCode());
    }

    @Test
    @DisplayName("Should validate valid person without violations")
    void testValidPersonValidation() {
        Person person = createPerson("F", DATE_1985, FIRST_MARIA, LAST_GONZALEZ, DOC_MARIA, DT_TI, CITY_BOGOTA);

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject invalid sex values")
    void testInvalidSexValidation() {
        Person person = createPerson("X", DATE_1985, FIRST_MARIA, LAST_GONZALEZ, DOC_MARIA, DT_TI, CITY_BOGOTA);

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(1, violations.size());

        ConstraintViolation<Person> violation = violations.iterator().next();
        assertEquals("Sex must be M or F", violation.getMessage());
        assertEquals("sex", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should reject blank sex")
    void testBlankSexValidation() {
        Person person = createPerson("", DATE_1985, FIRST_MARIA, LAST_GONZALEZ, DOC_MARIA, DT_TI, CITY_BOGOTA);

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(2, violations.size()); // NotBlank and Pattern violations
    }

    @Test
    @DisplayName("Should reject null sex")
    void testNullSexValidation() {
        Person person = createPerson(null, DATE_1985, FIRST_MARIA, LAST_GONZALEZ, DOC_MARIA, DT_TI, CITY_BOGOTA);

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(1, violations.size());

        ConstraintViolation<Person> violation = violations.iterator().next();
        assertEquals("Sex is required", violation.getMessage());
    }

    @Test
    @DisplayName("Should reject blank first name")
    void testBlankFirstNameValidation() {
        Person person = createPerson("F", DATE_1985, "", LAST_GONZALEZ, DOC_MARIA, DT_TI, CITY_BOGOTA);

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(1, violations.size());

        ConstraintViolation<Person> violation = violations.iterator().next();
        assertEquals("First name is required", violation.getMessage());
    }

    @Test
    @DisplayName("Should reject blank last name")
    void testBlankLastNameValidation() {
        Person person = createPerson("F", DATE_1985, FIRST_MARIA, "", DOC_MARIA, DT_TI, CITY_BOGOTA);

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(1, violations.size());

        ConstraintViolation<Person> violation = violations.iterator().next();
        assertEquals("Last name is required", violation.getMessage());
    }

    @Test
    @DisplayName("Should test getters and setters")
    void testGettersAndSetters() {
        Person person = new Person();

        person.setId(1L);
        person.setBirthDate(DATE_1990);
        person.setSex("M");
        person.setFirstName("Carlos");
        person.setLastName("Rodriguez");
        person.setCity("Cali");

        DocumentType docType = new DocumentType("CC", "Colombia");
        PersonDocument doc = new PersonDocument(person, "11111111", docType);
        person.addDocument(doc);

        assertEquals(1L, person.getId());
        assertEquals(DATE_1990, person.getBirthDate());
        assertEquals("M", person.getSex());
        assertEquals("Carlos", person.getFirstName());
        assertEquals("Rodriguez", person.getLastName());
        assertEquals("Cali", person.getCity());
        assertEquals("11111111", person.getDocumentNumber());
        assertEquals("CC", person.getDocumentType().getCode());
    }

    @Test
    @DisplayName("Should be equal to same object")
    void testEqualsSameObject() {
        Person person = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        assertEquals(person, person);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEqualsWithNull() {
        Person person = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        assertNotEquals(person, null);
    }

    @Test
    @DisplayName("Should not be equal to object of different class")
    void testEqualsWithDifferentClass() {
        Person person = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        String otherObject = "Not a Person";
        assertNotEquals(person, otherObject);
    }

    @Test
    @DisplayName("Should be equal when documents are same")
    void testEqualsWithSameDocument() {
        Person person1 = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        Person person2 = createPerson("F", DATE_1985, FIRST_MARIA, LAST_GONZALEZ, DOC_JUAN, DT_CC, CITY_BOGOTA);

        assertEquals(person1, person2);
    }

    @Test
    @DisplayName("Should not be equal when documents are different")
    void testEqualsWithDifferentDocument() {
        Person person1 = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        Person person2 = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_MARIA, DT_TI, CITY_MEDELLIN);

        assertNotEquals(person1, person2);
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void testHashCodeConsistency() {
        Person person = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        int hashCode1 = person.hashCode();
        int hashCode2 = person.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should have same hashCode for same document")
    void testHashCodeWithSameDocument() {
        Person person1 = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        Person person2 = createPerson("F", DATE_1985, FIRST_MARIA, LAST_GONZALEZ, DOC_JUAN, DT_CC, CITY_BOGOTA);

        assertEquals(person1.hashCode(), person2.hashCode());
    }

    @Test
    @DisplayName("Should not have same hashCode for different documents")
    void testHashCodeWithDifferentDocument() {
        Person person1 = createPerson("M", DATE_1990, FIRST_JUAN, LAST_PEREZ, DOC_JUAN, DT_CC, CITY_MEDELLIN);
        Person person2 = createPerson("F", DATE_1985, FIRST_MARIA, LAST_GONZALEZ, DOC_MARIA, DT_TI, CITY_BOGOTA);

        assertNotEquals(person1.hashCode(), person2.hashCode());
    }
}
