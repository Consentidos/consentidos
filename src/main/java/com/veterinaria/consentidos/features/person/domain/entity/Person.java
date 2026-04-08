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
    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^[MF]$", message = "Sex must be M or F")
    private String sex;

    @Column(name = "fecha_nacimiento", nullable = false)
    @NotNull(message = "Birth date is required")
    private LocalDateTime birthDate;

    @Column(name = "nombres", nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Column(name = "apellidos", nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Column(name = "documento", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Document is required")
    @Size(max = 50, message = "Document must not exceed 50 characters")
    private String document;

    @Column(name = "tipo_documento", nullable = false, length = 20)
    @NotBlank(message = "Document type is required")
    @Size(max = 20, message = "Document type must not exceed 20 characters")
    private String documentType;

    @Column(name = "ciudad", nullable = false, length = 100)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
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
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return document.equals(person.document) && documentType.equals(person.documentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, documentType);
    }
}