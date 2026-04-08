package com.veterinaria.consentidos.features.person.infrastructure.persistence;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.repository.PersonRepository;
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
}