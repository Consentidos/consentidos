package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.entity.PersonDocument;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds JPA Criteria predicates for dynamic Person queries.
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

        // JOIN on documents only when document-related criteria are present
        Join<Person, PersonDocument> docsJoin = null;

        if (criteria.getDocumentNumber() != null && !criteria.getDocumentNumber().isBlank()) {
            docsJoin = root.join("documents", JoinType.INNER);
            predicates.add(cb.isTrue(docsJoin.get("isActive")));
            predicates.add(cb.like(docsJoin.get("documentNumber"),
                    "%" + criteria.getDocumentNumber() + "%"));
        }
        if (criteria.getDocumentTypeId() != null) {
            if (docsJoin == null) {
                docsJoin = root.join("documents", JoinType.INNER);
                predicates.add(cb.isTrue(docsJoin.get("isActive")));
            }
            predicates.add(cb.equal(docsJoin.get("documentType").get("id"), criteria.getDocumentTypeId()));
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
