package com.veterinaria.consentidos.features.person.domain.exception;

public class PersonAlreadyExistsException extends RuntimeException {

    public PersonAlreadyExistsException(String document) {
        super("A person with document " + document + " already exists");
    }
}
