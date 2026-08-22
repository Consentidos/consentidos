package com.veterinaria.consentidos.features.documenttype.application.dto;

import com.veterinaria.consentidos.features.documenttype.domain.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for DocumentType responses.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeDto {

    private Long id;
    private String code;
    private String description;

    public static DocumentTypeDto fromEntity(DocumentType entity) {
        if (entity == null) return null;
        return new DocumentTypeDto(entity.getId(), entity.getCode(), entity.getDescription());
    }
}
