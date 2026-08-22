package com.veterinaria.consentidos.features.person.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.*;

/**
 * Person entity representing a person in the veterinary system.
 * Document history is managed via PersonDocument (one active at a time).
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

    @Column(name = "ciudad", nullable = false, length = CITY_MAX_LENGTH)
    @NotBlank(message = CITY_REQUIRED_MESSAGE)
    @Size(max = CITY_MAX_LENGTH, message = CITY_SIZE_MESSAGE)
    private String city;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonDocument> documents = new ArrayList<>();

    public Person() {
    }

    // Returns the currently active document, or null if none exists
    public PersonDocument getActiveDocument() {
        if (documents == null) return null;
        return documents.stream().filter(PersonDocument::isActive).findFirst().orElse(null);
    }

    // Convenience accessor delegating to the active document
    public String getDocumentNumber() {
        PersonDocument active = getActiveDocument();
        return active != null ? active.getDocumentNumber() : null;
    }

    // Returns the DocumentType entity of the active document
    public DocumentType getDocumentType() {
        PersonDocument active = getActiveDocument();
        return active != null ? active.getDocumentType() : null;
    }

    // Adds a new document and links it back to this person
    public void addDocument(PersonDocument document) {
        document.setPerson(this);
        documents.add(document);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person other)) return false;
        // Saved entities: compare by id
        if (id != null) return Objects.equals(id, other.id);
        // Unsaved entities: compare by active document number
        String myDoc = getDocumentNumber();
        String otherDoc = other.getDocumentNumber();
        if (myDoc == null) return false;
        return myDoc.equals(otherDoc);
    }

    @Override
    public int hashCode() {
        if (id != null) return Objects.hash(id);
        String doc = getDocumentNumber();
        return doc != null ? doc.hashCode() : 0;
    }
}