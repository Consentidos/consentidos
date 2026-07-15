package com.veterinaria.consentidos.features.person.domain.common;

import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.veterinaria.consentidos.features.person.domain.common.PersonValidationConstants.*;

/**
 * Value Object representing a person's identification.
 * Embeds the document number alongside a reference to the DocumentIdentifier catalog.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Document {

    @Column(name = "numero_documento", nullable = false, unique = true, length = DOCUMENT_MAX_LENGTH)
    @NotBlank(message = DOCUMENT_REQUIRED_MESSAGE)
    @Size(max = DOCUMENT_MAX_LENGTH, message = DOCUMENT_SIZE_MESSAGE)
    private String documentNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_identifier_id", nullable = false)
    @NotNull(message = DOCUMENT_IDENTIFIER_REQUIRED_MESSAGE)
    private DocumentIdentifier documentIdentifier;
}
