package com.veterinaria.consentidos.features.person.application.dto;

import com.veterinaria.consentidos.features.person.domain.entity.Person;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for Person responses.
 * Maps domain entity data for use by the application and presentation layers.
 */
@Getter
@Setter
@ToString
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

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
