package com.veterinaria.consentidos.features.person.application.command;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Person entity.
 * Contains all person data without validation constraints since it represents already validated data.
 */
public class PersonDto {

    private Long id;
    private String sex;
    private LocalDateTime birthDate;
    private String firstName;
    private String lastName;
    private String document;
    private String documentType;
    private String city;

    // Default constructor
    public PersonDto() {
    }

    // Constructor with all fields
    public PersonDto(Long id, String sex, LocalDateTime birthDate, String firstName, String lastName, String document, String documentType, String city) {
        this.id = id;
        this.sex = sex;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.documentType = documentType;
        this.city = city;
    }

    // Static factory method to create DTO from entity
    public static PersonDto fromEntity(Person person) {
        return new PersonDto(
                person.getId(),
                person.getSex(),
                person.getBirthDate(),
                person.getFirstName(),
                person.getLastName(),
                person.getDocument(),
                person.getDocumentType(),
                person.getCity()
        );
    }

    // Method to convert DTO to entity (for creation)
    public Person toEntity() {
        return new Person(sex, birthDate, firstName, lastName, document, documentType, city);
    }

    // Method to update existing entity with DTO data
    public void updateEntity(Person person) {
        person.setSex(this.sex);
        person.setBirthDate(this.birthDate);
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        person.setDocument(this.document);
        person.setDocumentType(this.documentType);
        person.setCity(this.city);
    }

    // Convenience method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    @Override
    public String toString() {
        return "PersonDto{" +
                "id=" + id +
                ", sex='" + sex + '\'' +
                ", birthDate=" + birthDate +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", document='" + document + '\'' +
                ", documentType='" + documentType + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}