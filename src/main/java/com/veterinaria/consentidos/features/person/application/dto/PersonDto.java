package com.veterinaria.consentidos.features.person.application.dto;

import com.veterinaria.consentidos.features.documenttype.application.dto.DocumentTypeDto;
import com.veterinaria.consentidos.features.person.domain.entity.Person;
import com.veterinaria.consentidos.features.person.domain.entity.PersonDocument;
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
    private String documentNumber;
    private DocumentTypeDto documentType;
    private String city;

    public static PersonDto fromEntity(Person person) {
        PersonDocument activeDoc = person.getActiveDocument();
        return new PersonDto(
                person.getId(),
                person.getSex(),
                person.getBirthDate(),
                person.getFirstName(),
                person.getLastName(),
                activeDoc != null ? activeDoc.getDocumentNumber() : null,
                activeDoc != null ? DocumentTypeDto.fromEntity(activeDoc.getDocumentType()) : null,
                person.getCity()
        );
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
