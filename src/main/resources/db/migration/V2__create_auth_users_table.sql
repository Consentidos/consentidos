-- ============================================================
-- V2 - Auth users table
-- ============================================================

CREATE TABLE auth_users (
    id            UUID         PRIMARY KEY,
    person_id     BIGINT       NOT NULL,
    google_sub    VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    display_name  VARCHAR(255) NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_login_at TIMESTAMP    NULL,
    CONSTRAINT uq_auth_users_person_id UNIQUE (person_id),
    CONSTRAINT uq_auth_users_google_sub UNIQUE (google_sub),
    CONSTRAINT fk_auth_users_person FOREIGN KEY (person_id) REFERENCES persons(id)
);

CREATE INDEX idx_auth_users_email ON auth_users (email);
CREATE INDEX idx_auth_users_active ON auth_users (active);

