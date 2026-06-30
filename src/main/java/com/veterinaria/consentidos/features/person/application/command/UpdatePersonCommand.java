package com.veterinaria.consentidos.features.person.application.command;

import com.veterinaria.consentidos.features.person.domain.common.BasePersonData;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Command object for updating an existing person.
 * Extends BasePersonData to avoid code duplication.
 */
@Getter
@Setter
@ToString(callSuper = true)
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
}