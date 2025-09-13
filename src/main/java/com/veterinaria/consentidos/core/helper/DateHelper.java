package com.veterinaria.consentidos.core.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date operations commonly used in veterinary applications
 */
public class DateHelper {
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT);
    
    /**
     * Formats a LocalDate to default format (yyyy-MM-dd)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DEFAULT_DATE_FORMATTER);
    }
    
    /**
     * Formats a LocalDate for display (dd/MM/yyyy)
     */
    public static String formatDateForDisplay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DISPLAY_DATE_FORMATTER);
    }
    
    /**
     * Calculates age in years from birth date (useful for pet age calculation)
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }
    
    /**
     * Calculates days between two dates (useful for treatment intervals)
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
}