package com.veterinaria.consentidos.features.person.domain.criteria;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Criteria object for dynamic person search.
 * All filter fields are optional; only non-null/non-blank fields are applied.
 * Pagination defaults to page 0 with 20 results per page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonSearchCriteria {

    private String firstName;
    private String lastName;
    private String document;
    private String documentType;
    private String sex;
    private String city;
    private LocalDateTime birthDateFrom;
    private LocalDateTime birthDateTo;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;
}
