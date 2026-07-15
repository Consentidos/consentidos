package com.veterinaria.consentidos.features.person.domain.exception;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(Long id) {
        super("Person with ID " + id + " not found");
    }

    public PersonNotFoundException(String document) {
        super("Person with document " + document + " not found");
    }
}
