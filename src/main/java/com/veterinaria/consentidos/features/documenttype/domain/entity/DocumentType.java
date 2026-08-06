package com.veterinaria.consentidos.features.documenttype.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Entity representing a type of identification document.
 * Acts as a catalog table with a unique code and a human-readable description.
 */
@Entity
@Table(name = "document_types")
@Data
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Document type code is required")
    @Size(max = 50, message = "Document type code must not exceed 50 characters")
    private String code;

    @Column(name = "description", nullable = false, length = 255)
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    public DocumentType() {
    }

    public DocumentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
