-- ============================================================
-- V1 - Esquema inicial del sistema de documentos
-- ============================================================

-- 1. Tabla de personas
CREATE TABLE persons (
    id               BIGSERIAL    PRIMARY KEY,
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    sexo             CHAR(1)      NOT NULL,
    fecha_nacimiento TIMESTAMP    NOT NULL,
    ciudad           VARCHAR(100) NOT NULL
);

-- 2. Tabla de tipos de documento
CREATE TABLE document_types (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT uq_document_types_code UNIQUE (code)
);

-- 3. Tabla de documentos por persona (histórico)
CREATE TABLE person_documents (
    id               BIGSERIAL   PRIMARY KEY,
    person_id        BIGINT      NOT NULL,
    document_number  VARCHAR(50) NOT NULL,
    document_type_id BIGINT      NOT NULL,
    is_active        BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_person_documents_person FOREIGN KEY (person_id)        REFERENCES persons(id),
    CONSTRAINT fk_person_documents_type   FOREIGN KEY (document_type_id) REFERENCES document_types(id),
    CONSTRAINT uq_person_documents_number UNIQUE (document_number)
);

-- 4. Índice parcial: garantiza solo un documento activo por persona a nivel BD
CREATE UNIQUE INDEX idx_person_documents_one_active
    ON person_documents(person_id)
 WHERE is_active = TRUE;

-- 5. Función del trigger
CREATE OR REPLACE FUNCTION fn_deactivate_previous_documents()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE person_documents
       SET is_active = FALSE
     WHERE person_id = NEW.person_id
       AND id        != NEW.id
       AND is_active = TRUE;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 6. Trigger: al insertar un nuevo documento activo, desactiva los anteriores
CREATE TRIGGER trg_deactivate_previous_documents
    AFTER INSERT ON person_documents
    FOR EACH ROW
    EXECUTE FUNCTION fn_deactivate_previous_documents();
