package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.AuthUser;
import com.veterinaria.consentidos.features.auth.domain.repository.JwtTokenService;
import com.veterinaria.consentidos.features.auth.infrastructure.config.AuthTokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.PostConstruct;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class JwtTokenServiceAdapter implements JwtTokenService {

    private static final String CLAIM_TYPE = "typ";
    private static final String CLAIM_SESSION_ID = "sid";

    private final AuthTokenProperties properties;
    private final AtomicReference<PrivateKey> cachedPrivateKey = new AtomicReference<>();
    private final AtomicReference<PublicKey> cachedPublicKey = new AtomicReference<>();

    public JwtTokenServiceAdapter(AuthTokenProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void initKeyCache() {
        cachedPrivateKey.set(parsePrivateKey(properties.getPrivateKeyPem()));
        cachedPublicKey.set(parsePublicKey(properties.getPublicKeyPem()));
    }

    @Override
    public SignedToken createAccessToken(AuthUser user, String sessionId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getAccessTtlSeconds());
        String tokenId = UUID.randomUUID().toString();

        String token = Jwts.builder()
            .id(tokenId)
            .subject(user.userId())
            .issuer(properties.getIssuer())
            .audience().add(properties.getAudience()).and()
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .claim(CLAIM_TYPE, "access")
            .claim(CLAIM_SESSION_ID, sessionId)
            .claim("google_sub", user.googleSub())
            .claim("email", user.email())
            // Roles/scopes stay empty until RBAC claims are introduced.
            .claim("roles", List.of())
            .claim("scope", List.of())
            .signWith(cachedPrivateKey.get(), Jwts.SIG.RS256)
            .compact();

        return new SignedToken(token, expiresAt, tokenId);
    }

    @Override
    public SignedToken createRefreshToken(AuthUser user, String sessionId, String tokenId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getRefreshTtlSeconds());

        String token = Jwts.builder()
            .id(tokenId)
            .subject(user.userId())
            .issuer(properties.getIssuer())
            .audience().add(properties.getAudience()).and()
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .claim(CLAIM_TYPE, "refresh")
            .claim(CLAIM_SESSION_ID, sessionId)
            .signWith(cachedPrivateKey.get(), Jwts.SIG.RS256)
            .compact();

        return new SignedToken(token, expiresAt, tokenId);
    }

    @Override
    public ParsedRefreshToken parseRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(cachedPublicKey.get())
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

            String type = claims.get(CLAIM_TYPE, String.class);
            if (!"refresh".equals(type)) {
                throw new SecurityException("Token is not a refresh token");
            }

            String userId = claims.getSubject();
            String sessionId = claims.get(CLAIM_SESSION_ID, String.class);
            String tokenId = claims.getId();

            return new ParsedRefreshToken(
                userId,
                sessionId,
                tokenId,
                claims.getExpiration().toInstant()
            );
        } catch (RuntimeException ex) {
            throw new SecurityException("Invalid refresh token", ex);
        }
    }

    private PrivateKey parsePrivateKey(String pem) {
        try {
            byte[] decoded = Base64.getDecoder().decode(normalizePem(pem));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid private key configuration", ex);
        }
    }

    private PublicKey parsePublicKey(String pem) {
        try {
            byte[] decoded = Base64.getDecoder().decode(normalizePem(pem));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid public key configuration", ex);
        }
    }

    private String normalizePem(String pem) {
        if (pem == null || pem.isBlank()) {
            throw new IllegalStateException("RSA key material is required");
        }

        return pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
    }
}

