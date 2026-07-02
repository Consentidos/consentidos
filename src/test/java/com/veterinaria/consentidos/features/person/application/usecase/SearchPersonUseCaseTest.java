package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.core.PagedResult;
import com.veterinaria.consentidos.features.person.application.command.PersonDto;
import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchPersonUseCase Tests")
class SearchPersonUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private SearchPersonUseCase searchPersonUseCase;

    @Test
    @DisplayName("Should return mapped paged result when persons are found")
    void testExecute_WithResults_ShouldReturnMappedPagedResult() {
        // Given
        Person person = new Person("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
        person.setId(1L);
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().city("Medellin").build();
        PagedResult<Person> repoResult = PagedResult.of(Arrays.asList(person), 0, 20, 1L);
        when(personRepository.search(criteria)).thenReturn(repoResult);

        // When
        PagedResult<PersonDto> result = searchPersonUseCase.execute(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Juan", result.getContent().get(0).getFirstName());
        assertEquals("Perez", result.getContent().get(0).getLastName());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(personRepository).search(criteria);
    }

    @Test
    @DisplayName("Should return empty paged result when no persons are found")
    void testExecute_WithEmptyResults_ShouldReturnEmptyPagedResult() {
        // Given
        PersonSearchCriteria criteria = new PersonSearchCriteria();
        PagedResult<Person> repoResult = PagedResult.of(Collections.emptyList(), 0, 20, 0L);
        when(personRepository.search(criteria)).thenReturn(repoResult);

        // When
        PagedResult<PersonDto> result = searchPersonUseCase.execute(criteria);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(personRepository).search(criteria);
    }

    @Test
    @DisplayName("Should preserve pagination metadata from repository result")
    void testExecute_ShouldPreservePaginationMetadata() {
        // Given
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().page(2).size(5).build();
        PagedResult<Person> repoResult = PagedResult.of(Collections.emptyList(), 2, 5, 30L);
        when(personRepository.search(criteria)).thenReturn(repoResult);

        // When
        PagedResult<PersonDto> result = searchPersonUseCase.execute(criteria);

        // Then
        assertEquals(2, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(30L, result.getTotalElements());
        assertEquals(6, result.getTotalPages());
        verify(personRepository).search(criteria);
    }
}
