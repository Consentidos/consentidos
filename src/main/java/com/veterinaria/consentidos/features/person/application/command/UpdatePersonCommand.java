package com.veterinaria.consentidos.features.person.application.command;

import com.veterinaria.consentidos.features.person.domain.common.BasePersonData;
import jakarta.validation.constraints.NotNull;

/**
 * Command object for updating an existing person.
 * Extends BasePersonData to avoid code duplication.
 */
public class UpdatePersonCommand extends BasePersonData {

    @NotNull(message = "ID is required")
    private Long id;

    // Default constructor
    public UpdatePersonCommand() {
        super();
    }

    // Constructor with all fields
    public UpdatePersonCommand(Long id, String sex, String firstName, String lastName, String city, String document, String documentType) {
        super(sex, firstName, lastName, city, document, documentType);
        this.id = id;
    }

    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UpdatePersonCommand{" +
                "id=" + id +
                ", sex='" + sex + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", document='" + document + '\'' +
                ", documentType='" + documentType + '\'' +
                '}';
    }
}