package com.veterinaria.consentidos.features.documentidentifier.domain.entity;

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
 * Acts as a catalog table for valid document types and their issuing countries.
 */
@Entity
@Table(name = "document_identifiers")
@Data
public class DocumentIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_type", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Document type is required")
    @Size(max = 50, message = "Document type must not exceed 50 characters")
    private String documentType;

    @Column(name = "document_country", nullable = false, length = 100)
    @NotBlank(message = "Document country is required")
    @Size(max = 100, message = "Document country must not exceed 100 characters")
    private String documentCountry;

    public DocumentIdentifier() {
    }

    public DocumentIdentifier(String documentType, String documentCountry) {
        this.documentType = documentType;
        this.documentCountry = documentCountry;
    }
}
