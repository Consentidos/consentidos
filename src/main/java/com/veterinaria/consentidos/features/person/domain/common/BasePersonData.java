package com.veterinaria.consentidos.features.person.domain.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.*;

/**
 * Base class for Person-related objects to avoid code duplication.
 * Contains common fields, validation annotations, and accessor methods.
 */
public abstract class BasePersonData {

    @NotBlank(message = SEX_REQUIRED_MESSAGE)
    @Pattern(regexp = SEX_PATTERN, message = SEX_PATTERN_MESSAGE)
    protected String sex;

    @NotBlank(message = FIRST_NAME_REQUIRED_MESSAGE)
    @Size(max = FIRST_NAME_MAX_LENGTH, message = FIRST_NAME_SIZE_MESSAGE)
    protected String firstName;

    @NotBlank(message = LAST_NAME_REQUIRED_MESSAGE)
    @Size(max = LAST_NAME_MAX_LENGTH, message = LAST_NAME_SIZE_MESSAGE)
    protected String lastName;

    @NotBlank(message = CITY_REQUIRED_MESSAGE)
    @Size(max = CITY_MAX_LENGTH, message = CITY_SIZE_MESSAGE)
    protected String city;

    @NotBlank(message = DOCUMENT_REQUIRED_MESSAGE)
    @Size(max = DOCUMENT_MAX_LENGTH, message = DOCUMENT_SIZE_MESSAGE)
    protected String document;

    @NotBlank(message = DOCUMENT_TYPE_REQUIRED_MESSAGE)
    @Size(max = DOCUMENT_TYPE_MAX_LENGTH, message = DOCUMENT_TYPE_SIZE_MESSAGE)
    protected String documentType;

    // Protected constructor for subclasses
    protected BasePersonData() {
    }

    protected BasePersonData(String sex, String firstName, String lastName, String city, String document, String documentType) {
        this.sex = sex;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.document = document;
        this.documentType = documentType;
    }

    // Common getters and setters
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    // Utility method available to all subclasses
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Common toString implementation
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "sex='" + sex + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", document='" + document + '\'' +
                ", documentType='" + documentType + '\'' +
                '}';
    }
}