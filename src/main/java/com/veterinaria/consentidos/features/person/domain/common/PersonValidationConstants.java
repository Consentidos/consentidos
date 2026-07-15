package com.veterinaria.consentidos.features.person.domain.common;

/**
 * Constants for Person validation rules.
 * Centralizes all validation constraints to avoid duplication.
 */
public final class PersonValidationConstants {
    
    private PersonValidationConstants() {
        // Utility class
    }

    // Shared suffix for size messages
    private static final String CHARACTERS_SUFFIX = " characters";
    
    // Field validation patterns
    public static final String SEX_PATTERN = "^[MF]$";
    
    // Size limits
    public static final int FIRST_NAME_MAX_LENGTH = 100;
    public static final int LAST_NAME_MAX_LENGTH = 100;
    public static final int CITY_MAX_LENGTH = 100;
    public static final int DOCUMENT_MAX_LENGTH = 50;
    public static final int DOCUMENT_TYPE_MAX_LENGTH = 20;
    
    // Error messages
    public static final String SEX_REQUIRED_MESSAGE = "Sex is required";
    public static final String SEX_PATTERN_MESSAGE = "Sex must be M or F";
    public static final String BIRTH_DATE_REQUIRED_MESSAGE = "Birth date is required";
    public static final String FIRST_NAME_REQUIRED_MESSAGE = "First name is required";
    public static final String FIRST_NAME_SIZE_MESSAGE = "First name must not exceed " + FIRST_NAME_MAX_LENGTH + CHARACTERS_SUFFIX;
    public static final String LAST_NAME_REQUIRED_MESSAGE = "Last name is required";
    public static final String LAST_NAME_SIZE_MESSAGE = "Last name must not exceed " + LAST_NAME_MAX_LENGTH + CHARACTERS_SUFFIX;
    public static final String CITY_REQUIRED_MESSAGE = "City is required";
    public static final String CITY_SIZE_MESSAGE = "City must not exceed " + CITY_MAX_LENGTH + CHARACTERS_SUFFIX;
    public static final String DOCUMENT_REQUIRED_MESSAGE = "Document is required";
    public static final String DOCUMENT_SIZE_MESSAGE = "Document must not exceed " + DOCUMENT_MAX_LENGTH + CHARACTERS_SUFFIX;
    public static final String DOCUMENT_TYPE_REQUIRED_MESSAGE = "Document type is required";
    public static final String DOCUMENT_TYPE_SIZE_MESSAGE = "Document type must not exceed " + DOCUMENT_TYPE_MAX_LENGTH + CHARACTERS_SUFFIX;
}