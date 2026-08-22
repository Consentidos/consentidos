package com.veterinaria.consentidos.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PagedResult Tests")
class PagedResultTest {

    @Test
    @DisplayName("Should calculate total pages correctly for exact division")
    void testOf_ExactDivision_ShouldCalculateTotalPages() {
        List<String> content = Arrays.asList("a", "b", "c", "d", "e");
        PagedResult<String> result = PagedResult.of(content, 0, 5, 10L);

        assertEquals(content, result.getContent());
        assertEquals(0, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(10L, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    @DisplayName("Should round up total pages when last page is partial")
    void testOf_PartialLastPage_ShouldRoundUpTotalPages() {
        PagedResult<String> result = PagedResult.of(Collections.emptyList(), 1, 5, 11L);

        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(11L, result.getTotalElements());
    }

    @Test
    @DisplayName("Should return zero total pages when total elements is zero")
    void testOf_ZeroElements_ShouldReturnZeroTotalPages() {
        PagedResult<String> result = PagedResult.of(Collections.emptyList(), 0, 10, 0L);

        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should return zero total pages when size is zero")
    void testOf_ZeroSize_ShouldReturnZeroTotalPages() {
        PagedResult<String> result = PagedResult.of(Collections.emptyList(), 0, 0, 10L);

        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName("Should preserve all values in the result object")
    void testOf_ShouldPreserveAllValues() {
        List<Integer> content = Arrays.asList(1, 2, 3);
        PagedResult<Integer> result = PagedResult.of(content, 2, 3, 9L);

        assertEquals(content, result.getContent());
        assertEquals(2, result.getPage());
        assertEquals(3, result.getSize());
        assertEquals(9L, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
    }
}
