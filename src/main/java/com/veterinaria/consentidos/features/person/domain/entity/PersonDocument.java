package com.veterinaria.consentidos.features.person.domain.entity;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.DOCUMENT_MAX_LENGTH;
import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.DOCUMENT_REQUIRED_MESSAGE;
import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.DOCUMENT_SIZE_MESSAGE;
import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.DOCUMENT_TYPE_REQUIRED_MESSAGE;

/**
 * Entity representing a person's identification document.
 * Each record is immutable once created; updates generate a new row.
 * Only one record per person may have is_active = true (enforced by DB trigger and partial unique index).
 */
@Entity
@Table(name = "person_documents")
@Getter
@Setter
@NoArgsConstructor
public class PersonDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "document_number", nullable = false, unique = true, length = DOCUMENT_MAX_LENGTH)
    @NotBlank(message = DOCUMENT_REQUIRED_MESSAGE)
    @Size(max = DOCUMENT_MAX_LENGTH, message = DOCUMENT_SIZE_MESSAGE)
    private String documentNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_type_id", nullable = false)
    @NotNull(message = DOCUMENT_TYPE_REQUIRED_MESSAGE)
    private DocumentType documentType;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(java.time.ZoneOffset.UTC);

    public PersonDocument(Person person, String documentNumber, DocumentType documentType) {
        this.person = person;
        this.documentNumber = documentNumber;
        this.documentType = documentType;
        this.isActive = true;
        this.createdAt = LocalDateTime.now(java.time.ZoneOffset.UTC);
    }
}
