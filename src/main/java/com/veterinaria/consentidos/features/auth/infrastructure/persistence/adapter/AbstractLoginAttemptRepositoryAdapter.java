package com.veterinaria.consentidos.features.auth.infrastructure.persistence.adapter;

import com.veterinaria.consentidos.features.auth.domain.entity.LoginAttemptState;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractLoginAttemptRepositoryAdapter {

    protected record LoginAttemptBuildInput(
        String key,
        String userHint,
        String deviceId,
        String clientIp,
        String userAgent,
        int attempts,
        Instant windowStartedAt,
        Instant lastAttemptAt,
        Instant blockedUntil,
        Instant expiresAt
    ) {
    }

    protected LoginAttemptState buildState(LoginAttemptBuildInput input) {
        return new LoginAttemptState(
            input.key(),
            input.userHint(),
            input.deviceId(),
            hashMetadata(input.clientIp()),
            hashMetadata(input.userAgent()),
            input.attempts(),
            input.windowStartedAt(),
            input.lastAttemptAt(),
            input.blockedUntil(),
            input.expiresAt()
        );
    }

    protected Map<String, String> toStorageMap(LoginAttemptState state) {
        Map<String, String> values = new HashMap<>();
        values.put("key", state.key());
        values.put("userHint", nullToBlank(state.userHint()));
        values.put("deviceId", nullToBlank(state.deviceId()));
        values.put("ipHash", nullToBlank(state.ipHash()));
        values.put("userAgentHash", nullToBlank(state.userAgentHash()));
        values.put("attempts", Integer.toString(state.attempts()));
        values.put("windowStartedAt", state.windowStartedAt().toString());
        values.put("lastAttemptAt", state.lastAttemptAt().toString());
        values.put("blockedUntil", state.blockedUntil() == null ? "" : state.blockedUntil().toString());
        values.put("expiresAt", state.expiresAt().toString());
        return values;
    }

    protected String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    protected Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Instant.parse(value);
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    private String hashMetadata(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to hash login attempt metadata", ex);
        }
    }
}
