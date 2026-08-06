package com.veterinaria.consentidos.features.person.application.command;

import com.veterinaria.consentidos.features.person.domain.common.BasePersonData;
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

    private Long id;

    // Default constructor
    public UpdatePersonCommand() {
        super();
    }

    // Constructor with all fields
    public UpdatePersonCommand(Long id, String sex, String firstName, String lastName, String city, String documentNumber, Long documentTypeId) {
        super(sex, firstName, lastName, city, documentNumber, documentTypeId);
        this.id = id;
    }
}