package com.veterinaria.consentidos.features.person.application.usecase;

import com.veterinaria.consentidos.core.PagedResult;
import com.veterinaria.consentidos.features.person.application.command.PersonDto;
import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

/**
 * Use case for dynamic person search using criteria.
 * Delegates to the repository and maps results to DTOs.
 */
@Service
public class SearchPersonUseCase {

    private final PersonRepository personRepository;

    @Autowired
    public SearchPersonUseCase(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public PagedResult<PersonDto> execute(PersonSearchCriteria criteria) {
        PagedResult<com.veterinaria.consentidos.features.person.domain.entity.Person> result =
                personRepository.search(criteria);
        return PagedResult.of(
                result.getContent().stream().map(PersonDto::fromEntity).collect(Collectors.toList()),
                result.getPage(),
                result.getSize(),
                result.getTotalElements()
        );
    }
}
