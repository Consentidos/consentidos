package com.veterinaria.consentidos.features.auth.domain.entity;

import java.time.Instant;

public record LoginAttemptState(
	String key,
	String userHint,
	String deviceId,
	String ipHash,
	String userAgentHash,
	int attempts,
	Instant windowStartedAt,
	Instant lastAttemptAt,
	Instant blockedUntil,
	Instant expiresAt
) {
	public boolean isBlocked(Instant now) {
		return blockedUntil != null && blockedUntil.isAfter(now);
	}

	public boolean isExpired(Instant now) {
		return expiresAt != null && !expiresAt.isAfter(now);
	}
}

