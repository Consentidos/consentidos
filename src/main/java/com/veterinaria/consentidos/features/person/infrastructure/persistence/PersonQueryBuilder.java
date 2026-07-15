package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds JPA Criteria predicates for dynamic Person queries.
 * Separates query construction from query execution in PersonRepositoryImpl.
 */
@Component
public class PersonQueryBuilder {

    public List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Person> root, PersonSearchCriteria criteria) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getFirstName() != null && !criteria.getFirstName().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("firstName")),
                    "%" + criteria.getFirstName().toLowerCase() + "%"));
        }
        if (criteria.getLastName() != null && !criteria.getLastName().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("lastName")),
                    "%" + criteria.getLastName().toLowerCase() + "%"));
        }
        if (criteria.getDocumentNumber() != null && !criteria.getDocumentNumber().isBlank()) {
            predicates.add(cb.like(root.get("identification").get("documentNumber"),
                    "%" + criteria.getDocumentNumber() + "%"));
        }
        if (criteria.getDocumentIdentifierId() != null) {
            predicates.add(cb.equal(
                    root.get("identification").get("documentIdentifier").get("id"),
                    criteria.getDocumentIdentifierId()));
        }
        if (criteria.getSex() != null && !criteria.getSex().isBlank()) {
            predicates.add(cb.equal(root.get("sex"), criteria.getSex()));
        }
        if (criteria.getCity() != null && !criteria.getCity().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("city")),
                    "%" + criteria.getCity().toLowerCase() + "%"));
        }
        if (criteria.getBirthDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), criteria.getBirthDateFrom()));
        }
        if (criteria.getBirthDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), criteria.getBirthDateTo()));
        }

        return predicates;
    }
}
