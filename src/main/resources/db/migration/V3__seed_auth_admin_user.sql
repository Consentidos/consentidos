-- ============================================================
-- V3 - Seed auth admin user (person + auth user)
-- ============================================================
-- Notes:
-- 1) This migration is create-only.
-- 2) It fails fast if google_sub or email already exists in auth_users.
-- 3) Replace placeholder values before running in shared environments.

DO $$
DECLARE
    v_person_id BIGINT;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM auth_users
        WHERE google_sub = 'REPLACE_ME_GOOGLE_SUB'
    ) THEN
        RAISE EXCEPTION 'Seed aborted: google_sub already exists in auth_users.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM auth_users
        WHERE email = 'admin.seed@gmail.com'
    ) THEN
        RAISE EXCEPTION 'Seed aborted: email already exists in auth_users.';
    END IF;

    INSERT INTO persons (
        nombres,
        apellidos,
        sexo,
        fecha_nacimiento,
        ciudad
    ) VALUES (
        'Auth',
        'Admin',
        'M',
        TIMESTAMP '1990-01-01 00:00:00',
        'Asuncion'
    )
    RETURNING id INTO v_person_id;

    INSERT INTO auth_users (
        id,
        person_id,
        google_sub,
        email,
        display_name,
        active,
        created_at,
        updated_at,
        last_login_at
    ) VALUES (
        '11111111-1111-1111-1111-111111111111',
        v_person_id,
        'REPLACE_ME_GOOGLE_SUB',
        'admin.seed@gmail.com',
        'Auth Admin Seed',
        TRUE,
        NOW(),
        NOW(),
        NULL
    );
END $$;

