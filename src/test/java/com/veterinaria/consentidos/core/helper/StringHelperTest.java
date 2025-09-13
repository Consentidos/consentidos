package com.veterinaria.consentidos.core.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringHelper Tests")
class StringHelperTest {
    
    @Test
    @DisplayName("Should return true for empty or null strings")
    void testIsEmpty() {
        assertTrue(StringHelper.isEmpty(null));
        assertTrue(StringHelper.isEmpty(""));
        assertTrue(StringHelper.isEmpty("   "));
        assertFalse(StringHelper.isEmpty("test"));
    }
    
    @Test
    @DisplayName("Should return true for non-empty strings")
    void testIsNotEmpty() {
        assertFalse(StringHelper.isNotEmpty(null));
        assertFalse(StringHelper.isNotEmpty(""));
        assertFalse(StringHelper.isNotEmpty("   "));
        assertTrue(StringHelper.isNotEmpty("test"));
    }
    
    @Test
    @DisplayName("Should capitalize first letter of each word")
    void testCapitalize() {
        assertNull(StringHelper.capitalize(null));
        assertEquals("", StringHelper.capitalize(""));
        assertEquals("Test String", StringHelper.capitalize("test string"));
        assertEquals("Test  Multiple  Spaces", StringHelper.capitalize("test  multiple  spaces"));
        assertEquals("Already Capitalized", StringHelper.capitalize("already capitalized"));
        assertEquals("Mixed Case", StringHelper.capitalize("mIXED cASE"));
    }
    
    @Test
    @DisplayName("Should validate email addresses correctly")
    void testIsValidEmail() {
        assertFalse(StringHelper.isValidEmail(null));
        assertFalse(StringHelper.isValidEmail(""));
        assertFalse(StringHelper.isValidEmail("invalid-email"));
        assertFalse(StringHelper.isValidEmail("test@"));
        assertFalse(StringHelper.isValidEmail("@domain.com"));
        assertTrue(StringHelper.isValidEmail("test@example.com"));
        assertTrue(StringHelper.isValidEmail("user.name+tag@domain.co.uk"));
    }
    
    @Test
    @DisplayName("Should validate phone numbers correctly")
    void testIsValidPhone() {
        assertFalse(StringHelper.isValidPhone(null));
        assertFalse(StringHelper.isValidPhone(""));
        assertFalse(StringHelper.isValidPhone("123"));
        assertFalse(StringHelper.isValidPhone("abc123"));
        assertTrue(StringHelper.isValidPhone("1234567890"));
        assertTrue(StringHelper.isValidPhone("+1234567890"));
        assertTrue(StringHelper.isValidPhone("123-456-7890"));
        assertTrue(StringHelper.isValidPhone("(123) 456-7890"));
    }
}