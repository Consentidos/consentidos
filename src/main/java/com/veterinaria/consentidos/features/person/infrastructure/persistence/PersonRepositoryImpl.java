package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.core.PagedResult;
import com.veterinaria.consentidos.features.person.domain.criteria.PersonSearchCriteria;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of PersonRepository using JPA.
 * This class acts as an adapter between the domain repository interface
 * and the Spring Data JPA repository, following the Hexagonal Architecture pattern.
 */
@Component
public class PersonRepositoryImpl implements PersonRepository {

    private final PersonJpaRepository personJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PersonRepositoryImpl(PersonJpaRepository personJpaRepository) {
        this.personJpaRepository = personJpaRepository;
    }

    @Override
    public Person save(Person person) {
        return personJpaRepository.save(person);
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personJpaRepository.findById(id);
    }

    @Override
    public Optional<Person> findByDocument(String document) {
        return personJpaRepository.findByDocument(document);
    }

    @Override
    public List<Person> findAll() {
        return personJpaRepository.findAll();
    }

    @Override
    public List<Person> findByCity(String city) {
        return personJpaRepository.findByCityIgnoreCase(city);
    }

    @Override
    public List<Person> findByDocumentType(String documentType) {
        return personJpaRepository.findByDocumentTypeIgnoreCase(documentType);
    }

    @Override
    public boolean existsByDocument(String document) {
        return personJpaRepository.existsByDocument(document);
    }

    @Override
    public void deleteById(Long id) {
        personJpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return personJpaRepository.count();
    }

    @Override
    public PagedResult<Person> search(PersonSearchCriteria criteria) {
        int page = Math.max(0, criteria.getPage());
        int size = Math.max(1, criteria.getSize());

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Count query for total elements
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Person> countRoot = countQuery.from(Person.class);
        countQuery.select(cb.count(countRoot))
                .where(cb.and(buildPredicates(cb, countRoot, criteria).toArray(new Predicate[0])));
        long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        // Data query with pagination
        CriteriaQuery<Person> dataQuery = cb.createQuery(Person.class);
        Root<Person> dataRoot = dataQuery.from(Person.class);
        dataQuery.where(cb.and(buildPredicates(cb, dataRoot, criteria).toArray(new Predicate[0])));
        List<Person> content = entityManager.createQuery(dataQuery)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return PagedResult.of(content, page, size, totalElements);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Person> root, PersonSearchCriteria criteria) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getFirstName() != null && !criteria.getFirstName().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("firstName")),
                    "%" + criteria.getFirstName().toLowerCase() + "%"));
        }
        if (criteria.getLastName() != null && !criteria.getLastName().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("lastName")),
                    "%" + criteria.getLastName().toLowerCase() + "%"));
        }
        if (criteria.getDocument() != null && !criteria.getDocument().isBlank()) {
            predicates.add(cb.like(root.get("document"),
                    "%" + criteria.getDocument() + "%"));
        }
        if (criteria.getDocumentType() != null && !criteria.getDocumentType().isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("documentType")),
                    criteria.getDocumentType().toLowerCase()));
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