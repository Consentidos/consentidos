package com.veterinaria.consentidos.features.person.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Objects;

import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import com.veterinaria.consentidos.features.person.domain.common.Document;
import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.*;

/**
 * Person entity representing a person in the veterinary system.
 * This entity stores basic personal information including identification details.
 */
@Entity
@Table(name = "persons")
@Data
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

    @Valid
    @Embedded
    private Document identification;

    @Column(name = "ciudad", nullable = false, length = CITY_MAX_LENGTH)
    @NotBlank(message = CITY_REQUIRED_MESSAGE)
    @Size(max = CITY_MAX_LENGTH, message = CITY_SIZE_MESSAGE)
    private String city;

    // Default constructor for JPA
    public Person() {
    }

    // Constructor for creating new persons
    public Person(String sex, LocalDateTime birthDate, String firstName, String lastName, String documentNumber, DocumentIdentifier documentIdentifier, String city) {
        this.sex = sex;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identification = new Document(documentNumber, documentIdentifier);
        this.city = city;
    }

    // Convenience accessors — delegate to the Document value object
    public String getDocumentNumber() {
        return identification != null ? identification.getDocumentNumber() : null;
    }

    public void setDocumentNumber(String documentNumber) {
        if (this.identification == null) this.identification = new Document();
        this.identification.setDocumentNumber(documentNumber);
    }

    public DocumentIdentifier getDocumentIdentifier() {
        return identification != null ? identification.getDocumentIdentifier() : null;
    }

    public void setDocumentIdentifier(DocumentIdentifier documentIdentifier) {
        if (this.identification == null) this.identification = new Document();
        this.identification.setDocumentIdentifier(documentIdentifier);
    }

    public String getDocumentType() {
        return identification != null && identification.getDocumentIdentifier() != null
                ? identification.getDocumentIdentifier().getDocumentType() : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person person)) return false;
        return Objects.equals(identification, person.identification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identification);
    }
}