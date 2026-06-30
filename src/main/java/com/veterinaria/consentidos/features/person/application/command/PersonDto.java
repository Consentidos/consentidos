package com.veterinaria.consentidos.features.person.application.command;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object for Person entity.
 * Contains all person data without validation constraints since it represents already validated data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {

    private Long id;
    private String sex;
    private LocalDateTime birthDate;
    private String firstName;
    private String lastName;
    private String document;
    private String documentType;
    private String city;

    // Static factory method to create DTO from entity
    public static PersonDto fromEntity(Person person) {
        return new PersonDto(
                person.getId(),
                person.getSex(),
                person.getBirthDate(),
                person.getFirstName(),
                person.getLastName(),
                person.getDocument(),
                person.getDocumentType(),
                person.getCity()
        );
    }

    // Method to convert DTO to entity (for creation)
    public Person toEntity() {
        return new Person(sex, birthDate, firstName, lastName, document, documentType, city);
    }

    // Method to update existing entity with DTO data
    public void updateEntity(Person person) {
        person.setSex(this.sex);
        person.setBirthDate(this.birthDate);
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        person.setDocument(this.document);
        person.setDocumentType(this.documentType);
        person.setCity(this.city);
    }

    // Convenience method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

}