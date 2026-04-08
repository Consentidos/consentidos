package com.veterinaria.consentidos.features.person.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Command object for creating a new person.
 * This class encapsulates all the data needed to create a person
 * and includes validation annotations.
 */
public class CreatePersonCommand {

    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^[MF]$", message = "Sex must be M or F")
    private String sex;

    @NotNull(message = "Birth date is required")
    private LocalDateTime birthDate;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "Document is required")
    @Size(max = 50, message = "Document must not exceed 50 characters")
    private String document;

    @NotBlank(message = "Document type is required")
    @Size(max = 20, message = "Document type must not exceed 20 characters")
    private String documentType;

    // Default constructor
    public CreatePersonCommand() {
    }

    // Constructor with all fields
    public CreatePersonCommand(String sex, LocalDateTime birthDate, String firstName, String lastName, String city, String document, String documentType) {
        this.sex = sex;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.document = document;
        this.documentType = documentType;
    }

    // Getters and Setters
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
        return "CreatePersonCommand{" +
                "sex='" + sex + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", document='" + document + '\'' +
                ", documentType='" + documentType + '\'' +
                '}';
    }
}