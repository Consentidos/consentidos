# Auth Feature

Feature responsible for authentication and session management in Consentidos.

## Current scope

This feature currently implements:

- Google sign-in using `id_token`
- JWT access token issuance
- JWT refresh token issuance
- Refresh token rotation
- Logout by refresh token revocation
- Login attempt tracking and temporary lockout
- Single active session per user
- Pre-registered user validation from PostgreSQL in non-local profiles
- In-memory user/session/attempt stores for the `local` profile

## Public API

Base path:

- `/api/auth`

Available endpoints:

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

## Architecture

The feature follows a vertical-slice architecture with layered and hexagonal influences:

- `presentation` exposes HTTP endpoints and DTOs
- `application` contains commands and use cases
- `domain` defines entities and repository/service ports
- `infrastructure` provides configuration and concrete adapters

Dependency flow:

- `presentation -> application -> domain`
- `infrastructure` implements domain ports

## Main components

### Presentation
- `AuthController`
- Request DTOs:
  - `LoginRequestDto`
  - `RefreshTokenRequestDto`
  - `LogoutRequestDto`
- Response DTOs:
  - `AuthTokenResponseDto`
  - `LogoutResponseDto`

### Application
- `LoginWithGoogleUseCase`
- `RefreshSessionUseCase`
- `LogoutUseCase`
- Commands:
  - `LoginWithGoogleCommand`
  - `RefreshSessionCommand`
  - `LogoutCommand`

### Domain
- `AuthUser`
- `GoogleIdentity`
- `RefreshTokenSession`
- `TokenPair`
- `LoginAttemptState`
- `LoginAttemptPolicy`
- Ports:
  - `AuthUserRepository`
  - `GoogleIdentityVerifier`
  - `JwtTokenService`
  - `RefreshTokenSessionRepository`
  - `LoginAttemptRepository`

### Infrastructure
- `AuthTokenProperties`
- `GoogleAuthProperties`
- `RedisAuthProperties`
- `AuthLoginAttemptProperties`
- `PreRegisteredUsersProperties`
- Adapters:
  - `GoogleIdentityVerifierAdapter`
  - `PostgresAuthUserRepositoryAdapter`
  - `RedisRefreshTokenSessionRepositoryAdapter`
  - `RedisLoginAttemptRepositoryAdapter`
  - `InMemoryAuthUserRepositoryAdapter`
  - `InMemoryRefreshTokenSessionRepositoryAdapter`
  - `InMemoryLoginAttemptRepositoryAdapter`
  - `JwtTokenServiceAdapter`

## Profiles

- `local`: in-memory auth users, refresh sessions, and login attempts
- `local,external-storage`: PostgreSQL users + Redis sessions/attempts (dev-like behavior)
- `dev` and other non-local profiles: PostgreSQL users + Redis sessions/attempts

## Authentication flow

### Login
1. Client sends a Google `id_token` and `X-Device-Id`
2. `GoogleIdentityVerifier` validates the token and checks allowed Google domains
3. The user is looked up in PostgreSQL or the local in-memory store
4. The previous active session for that user is revoked
5. Access and refresh JWTs are generated
6. The refresh session is stored in Redis or memory with TTL
7. Login attempts are cleared on success

### Refresh
1. Client sends a refresh token
2. The token is parsed and validated
3. The refresh session is loaded from Redis or memory
4. The active session for that user is verified
5. The old refresh token is revoked
6. A new token pair is issued and stored

### Logout
1. Client sends a refresh token
2. The token is parsed
3. The corresponding session is revoked

## Token strategy

- Access and refresh tokens are JWTs
- Tokens are RSA signed
- Token metadata is controlled by `auth.token` properties
- Access token TTL: 15 minutes
- Refresh token TTL: 7 days
- Refresh tokens are tracked by token id and session id

## Configuration

### `auth.google`
Used to configure allowed Google client IDs and allowed Google domains.

### `auth.redis`
Used to configure Lettuce Redis connectivity for non-local profiles.

### `auth.login-attempts`
Used to configure login throttling and lockout behavior.

### `auth.token`
Used to configure:
- issuer
- audience
- access token TTL
- refresh token TTL
- RSA private/public key material

## Notes

- Storage is in-memory only when the `local` profile is active.
- Dev and higher environments should use PostgreSQL + Redis.
- RBAC, `/me`, and logout-all are still future work.
