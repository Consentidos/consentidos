package com.veterinaria.consentidos.features.person.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.*;

/**
 * Person entity representing a person in the veterinary system.
 * This entity stores basic personal information including identification details.
 */
@Entity
@Table(name = "persons")
@ToString(includeFieldNames = true)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sexo", nullable = false, length = 1)
    @NotBlank(message = SEX_REQUIRED_MESSAGE)
    @Pattern(regexp = SEX_PATTERN, message = SEX_PATTERN_MESSAGE)
    private String sex;

    @Column(name = "fecha_nacimiento", nullable = false)
    @NotNull(message = BIRTH_DATE_REQUIRED_MESSAGE)
    private LocalDateTime birthDate;

    @Column(name = "nombres", nullable = false, length = FIRST_NAME_MAX_LENGTH)
    @NotBlank(message = FIRST_NAME_REQUIRED_MESSAGE)
    @Size(max = FIRST_NAME_MAX_LENGTH, message = FIRST_NAME_SIZE_MESSAGE)
    private String firstName;

    @Column(name = "apellidos", nullable = false, length = LAST_NAME_MAX_LENGTH)
    @NotBlank(message = LAST_NAME_REQUIRED_MESSAGE)
    @Size(max = LAST_NAME_MAX_LENGTH, message = LAST_NAME_SIZE_MESSAGE)
    private String lastName;

    @Column(name = "documento", nullable = false, unique = true, length = DOCUMENT_MAX_LENGTH)
    @NotBlank(message = DOCUMENT_REQUIRED_MESSAGE)
    @Size(max = DOCUMENT_MAX_LENGTH, message = DOCUMENT_SIZE_MESSAGE)
    private String document;

    @Column(name = "tipo_documento", nullable = false, length = DOCUMENT_TYPE_MAX_LENGTH)
    @NotBlank(message = DOCUMENT_TYPE_REQUIRED_MESSAGE)
    @Size(max = DOCUMENT_TYPE_MAX_LENGTH, message = DOCUMENT_TYPE_SIZE_MESSAGE)
    private String documentType;

    @Column(name = "ciudad", nullable = false, length = CITY_MAX_LENGTH)
    @NotBlank(message = CITY_REQUIRED_MESSAGE)
    @Size(max = CITY_MAX_LENGTH, message = CITY_SIZE_MESSAGE)
    private String city;

    // Default constructor for JPA
    public Person() {
    }

    // Constructor for creating new persons
    public Person(String sex, LocalDateTime birthDate, String firstName, String lastName, String document, String documentType, String city) {
        this.sex = sex;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.documentType = documentType;
        this.city = city;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person person)) return false;
        return Objects.equals(document, person.document)
                && Objects.equals(documentType, person.documentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, documentType);
    }
}