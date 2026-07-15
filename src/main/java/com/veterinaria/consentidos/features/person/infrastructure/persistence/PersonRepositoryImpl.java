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
    private final PersonQueryBuilder queryBuilder;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PersonRepositoryImpl(PersonJpaRepository personJpaRepository, PersonQueryBuilder queryBuilder) {
        this.personJpaRepository = personJpaRepository;
        this.queryBuilder = queryBuilder;
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
    public Optional<Person> findByDocument(String documentNumber) {
        return personJpaRepository.findByIdentification_DocumentNumber(documentNumber);
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
        return personJpaRepository.findByIdentification_DocumentIdentifier_DocumentTypeIgnoreCase(documentType);
    }

    @Override
    public boolean existsByDocument(String documentNumber) {
        return personJpaRepository.existsByIdentification_DocumentNumber(documentNumber);
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

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Person> countRoot = countQuery.from(Person.class);
        List<Predicate> countPredicates = queryBuilder.buildPredicates(cb, countRoot, criteria);
        countQuery.select(cb.count(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));
        long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        CriteriaQuery<Person> dataQuery = cb.createQuery(Person.class);
        Root<Person> dataRoot = dataQuery.from(Person.class);
        List<Predicate> dataPredicates = queryBuilder.buildPredicates(cb, dataRoot, criteria);
        dataQuery.where(cb.and(dataPredicates.toArray(new Predicate[0])));
        List<Person> content = entityManager.createQuery(dataQuery)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return PagedResult.of(content, page, size, totalElements);
    }
}