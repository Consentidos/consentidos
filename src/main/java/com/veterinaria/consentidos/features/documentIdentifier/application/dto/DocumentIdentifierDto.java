package com.veterinaria.consentidos.features.documentIdentifier.application.dto;

import com.veterinaria.consentidos.features.documentIdentifier.domain.entity.DocumentIdentifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for DocumentIdentifier responses.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentIdentifierDto {

    private Long id;
    private String documentType;
    private String documentCountry;

    public static DocumentIdentifierDto fromEntity(DocumentIdentifier entity) {
        if (entity == null) return null;
        return new DocumentIdentifierDto(entity.getId(), entity.getDocumentType(), entity.getDocumentCountry());
    }
}
