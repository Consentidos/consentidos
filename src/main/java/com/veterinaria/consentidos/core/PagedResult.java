package com.veterinaria.consentidos.core;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Generic paginated result wrapper.
 * Used across features to represent a page of results from a query.
 */
@Getter
@AllArgsConstructor
public class PagedResult<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return new PagedResult<>(content, page, size, totalElements, totalPages);
    }
}
