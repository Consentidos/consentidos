package com.veterinaria.consentidos.core.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateHelper Tests")
class DateHelperTest {
    
    @Test
    @DisplayName("Should format date correctly")
    void testFormatDate() {
        assertNull(DateHelper.formatDate(null));
        
        LocalDate date = LocalDate.of(2023, 12, 25);
        assertEquals("2023-12-25", DateHelper.formatDate(date));
    }
    
    @Test
    @DisplayName("Should format date for display correctly")
    void testFormatDateForDisplay() {
        assertNull(DateHelper.formatDateForDisplay(null));
        
        LocalDate date = LocalDate.of(2023, 12, 25);
        assertEquals("25/12/2023", DateHelper.formatDateForDisplay(date));
    }
    
    @Test
    @DisplayName("Should calculate age correctly")
    void testCalculateAge() {
        assertEquals(0, DateHelper.calculateAge(null));
        
        LocalDate birthDate = LocalDate.now().minusYears(5);
        assertEquals(5, DateHelper.calculateAge(birthDate));
        
        LocalDate recentBirth = LocalDate.now().minusMonths(6);
        assertEquals(0, DateHelper.calculateAge(recentBirth));
    }
    
    @Test
    @DisplayName("Should calculate days between dates correctly")
    void testDaysBetween() {
        assertEquals(0, DateHelper.daysBetween(null, null));
        assertEquals(0, DateHelper.daysBetween(LocalDate.now(), null));
        assertEquals(0, DateHelper.daysBetween(null, LocalDate.now()));
        
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 10);
        assertEquals(9, DateHelper.daysBetween(startDate, endDate));
        
        assertEquals(-9, DateHelper.daysBetween(endDate, startDate));
    }
}