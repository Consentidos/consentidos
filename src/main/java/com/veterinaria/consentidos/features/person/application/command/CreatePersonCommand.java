package com.veterinaria.consentidos.features.person.application.command;

import com.veterinaria.consentidos.features.person.domain.common.BasePersonData;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.BIRTH_DATE_REQUIRED_MESSAGE;

/**
 * Command object for creating a new person.
 * Extends BasePersonData to avoid code duplication.
 */
public class CreatePersonCommand extends BasePersonData {

    @NotNull(message = BIRTH_DATE_REQUIRED_MESSAGE)
    private LocalDateTime birthDate;

    // Default constructor
    public CreatePersonCommand() {
        super();
    }

    // Constructor with all fields
    public CreatePersonCommand(String sex, LocalDateTime birthDate, String firstName, String lastName, String city, String document, String documentType) {
        super(sex, firstName, lastName, city, document, documentType);
        this.birthDate = birthDate;
    }

    // Getter and Setter for birthDate
    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "CreatePersonCommand{" +
                "birthDate=" + birthDate +
                ", sex='" + sex + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", document='" + document + '\'' +
                ", documentType='" + documentType + '\'' +
                '}';
    }
}